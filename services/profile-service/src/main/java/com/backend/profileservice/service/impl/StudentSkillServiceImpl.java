package com.backend.profileservice.service.impl;

import com.backend.profileservice.client.SkillClient;
import com.backend.profileservice.dto.external.skill.CreateSkillRequest;
import com.backend.profileservice.dto.external.skill.SkillResponse;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.StudentSkillResponse;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.entity.StudentSkill;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.StudentSkillMapper;
import com.backend.profileservice.repository.StudentRepository;
import com.backend.profileservice.repository.StudentSkillRepository;
import com.backend.profileservice.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSkillServiceImpl implements StudentSkillService {

    private final StudentSkillRepository studentSkillRepository;
    private final StudentSkillMapper studentSkillMapper;
    private final StudentRepository studentRepository;
    private final SkillClient skillClient;

    @Override
    @PreAuthorize("isAuthenticated()")
    public StudentSkillResponse create(UUID userId, StudentSkillCreateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        UUID skillId = resolveSkillId(request);

        if(studentSkillRepository.existsByStudentIdAndSkillId(student.getId(), skillId)){
            throw new AppException(ErrorCode.STUDENT_SKILL_EXISTS);
        }

        StudentSkill studentSkill = studentSkillMapper.toEntity(request);
        studentSkill.setSkillId(skillId);
        studentSkill.setStudent(student);

        StudentSkill saved = studentSkillRepository.save(studentSkill);
        StudentSkillResponse response = studentSkillMapper.toResponse(saved);
        return addSkillInfo(response);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public StudentSkillResponse update (UUID userId, UUID studentSkillId, StudentSkillUpdateRequest request){
        StudentSkill studentSkill = studentSkillRepository.findByIdAndStudentUserId(studentSkillId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_SKILL_NOT_FOUND));

//        if (request.getSkillId() != null || request.getSkillName() != null) {
//            throw new AppException(ErrorCode.CANNOT_UPDATE_SKILL_ID_OR_NAME);
//        }

        studentSkillMapper.updateEntity(studentSkill, request);

        StudentSkill saved = studentSkillRepository.save(studentSkill);
        StudentSkillResponse response = studentSkillMapper.toResponse(saved);
        return addSkillInfo(response);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID studentSkillId){
        StudentSkill studentSkill = studentSkillRepository.findByIdAndStudentUserId(studentSkillId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_SKILL_NOT_FOUND));

        studentSkillRepository.delete(studentSkill);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<StudentSkillResponse> getAllByStudent(UUID userId){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentSkillRepository.findByStudentId(student.getId())
                .stream()
                .map(studentSkillMapper::toResponse)
                .map(this::addSkillInfo)
                .collect(Collectors.toList());
    }

    private UUID resolveSkillId(StudentSkillCreateRequest request){
        // Case 1 → FE chọn skill từ list (có skillId)
        if(request.getSkillId() != null){
            UUID id = UUID.fromString(request.getSkillId());

            ApiResponse<SkillResponse> api = skillClient.getSkillById(id);
            SkillResponse skill = api.getData();

            if(skill == null){
                throw new AppException(ErrorCode.SKILL_NOT_FOUND);
            }
            return id;
        }

        // Case 2 → FE nhập skillName → yêu cầu categoryId bắt buộc
        if(request.getSkillName() != null){
            if(request.getCategoryId() == null){
                throw new AppException(ErrorCode.CATEGORY_REQUIRED);
            }

            // Thử tìm skill tồn tại trước
            try{
                ApiResponse<SkillResponse> api = skillClient.getSkillByName(request.getSkillName());
                SkillResponse found = api.getData();
                if(found != null){
                    return found.getId();
                }
            } catch (Exception exception){

            }

            // Tạo mới skill nếu không tìm thấy
            CreateSkillRequest skill = new CreateSkillRequest();
            skill.setName(request.getSkillName());
            skill.setCategoryId(UUID.fromString(request.getCategoryId()));
            skill.setDescription(null);

            ApiResponse<SkillResponse> apiCreated = skillClient.createSkill(skill);
            SkillResponse created = apiCreated.getData();

            return created.getId();
        }

        throw new AppException(ErrorCode.SKILL_ID_OR_NAME_REQUIRED);
    }

    private StudentSkillResponse addSkillInfo(StudentSkillResponse response) {
        if (response.getSkillId() == null) return response;

        try {
            ApiResponse<SkillResponse> api = skillClient.getSkillById(response.getSkillId());
            SkillResponse skill = api.getData();
            if (skill != null) {
                response.setSkillName(skill.getName());
                response.setCategory(skill.getCategory().getName());
            }
        } catch (Exception ignored) { }

        return response;
    }
}
