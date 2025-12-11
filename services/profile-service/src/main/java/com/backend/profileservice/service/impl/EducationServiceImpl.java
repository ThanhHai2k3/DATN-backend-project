package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.student.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.student.EducationResponse;
import com.backend.profileservice.entity.Education;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.EducationMapper;
import com.backend.profileservice.repository.EducationRepository;
import com.backend.profileservice.repository.StudentRepository;
import com.backend.profileservice.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final StudentRepository studentRepository;

    @Override
    @PreAuthorize("isAuthenticated()")
    public EducationResponse create(UUID userId, EducationCreateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Education education = educationMapper.toEntity(request);
        education.setStudent(student);

        Education saved = educationRepository.save(education);
        return educationMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    // chỉ cần check login chưa, không cần check role vì findByIdAndStudentUserId đã check self-permission
    public EducationResponse update(UUID userId, UUID educationId, EducationUpdateRequest request){
        Education education = educationRepository.findByIdAndStudentUserId(educationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));

        educationMapper.updateEntity(education, request);

        Education saved = educationRepository.save(education);
        return educationMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<EducationResponse> getAllByStudent(UUID userId){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return educationRepository.findByStudentId(student.getId())
                .stream()
                .map(educationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID educationId){
        Education education = educationRepository.findByIdAndStudentUserId(educationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));

        educationRepository.delete(education);
    }
}
