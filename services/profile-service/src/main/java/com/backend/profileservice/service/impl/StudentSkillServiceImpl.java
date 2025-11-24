package com.backend.profileservice.service.impl;

import com.backend.profileservice.client.SkillClient;
import com.backend.profileservice.dto.external.skill.CreateSkillRequest;
import com.backend.profileservice.dto.external.skill.SkillResponse;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
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
        return studentSkillMapper.toResponse(saved);
    }

    @Override
    public StudentSkillResponse update (UUID userId, UUID studentSkillId, StudentSkillUpdateRequest request){
        StudentSkill studentSkill = studentSkillRepository.findById(studentSkillId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_SKILL_NOT_FOUND));

        if (!studentSkill.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

//        if (request.getSkillId() != null || request.getSkillName() != null) {
//            throw new AppException(ErrorCode.CANNOT_UPDATE_SKILL_ID_OR_NAME);
//        }

        studentSkillMapper.updateEntity(studentSkill, request);

        StudentSkill saved = studentSkillRepository.save(studentSkill);
        return studentSkillMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID studentSkillId, UUID userId){
        StudentSkill studentSkill = studentSkillRepository.findById(studentSkillId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_SKILL_NOT_FOUND));

        if (!studentSkill.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        studentSkillRepository.delete(studentSkill);
    }

    @Override
    public List<StudentSkillResponse> getAllByStudent(UUID userId){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentSkillRepository.findByStudentId(student.getId())
                .stream()
                .map(studentSkillMapper::toResponse)
                .collect(Collectors.toList());
    }

    private UUID resolveSkillId(StudentSkillCreateRequest request){
        // Case 1 → FE chọn skill từ list (có skillId)
        if(request.getSkillId() != null){
            UUID id = UUID.fromString(request.getSkillId());
            SkillResponse skill = skillClient.getSkillById(id);

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

            try{
                SkillResponse found = skillClient.getSkillByName(request.getSkillName());
                if(found != null){
                    return found.getId();
                }
            } catch (Exception exception){

            }

            CreateSkillRequest skill = new CreateSkillRequest();
            skill.setName(request.getSkillName());
            skill.setCategoryId(UUID.fromString(request.getCategoryId()));
            skill.setDescription(null);

            SkillResponse created = skillClient.createSkill(skill);
            return created.getId();
        }

        throw new AppException(ErrorCode.SKILL_ID_OR_NAME_REQUIRED);
    }
}
