package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.dto.response.student.VisibilityResponse;
import com.backend.profileservice.entity.*;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.StudentMapper;
import com.backend.profileservice.repository.*;
import com.backend.profileservice.service.StudentService;
import com.backend.profileservice.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final StudentSkillService studentSkillService;

    // SELF PROFILE
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public StudentResponse getByUserId(UUID userId){
        Student profile = studentRepository.findByUserIdWithAllRelations(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        StudentResponse response = studentMapper.toResponse(profile);

        response.setSkills(studentSkillService.getAllByStudent(userId));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getBasicInfoBatch(List<UUID> userIds) {
        List<Student> students = studentRepository.findByUserIdIn(userIds);
        // Reuse mapper có sẵn để chuyển đổi sang Response
        return students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public StudentResponse updateProfile(UUID userId, StudentUpdateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        studentMapper.updateEntity(student, request);

        Student saved = studentRepository.save(student);
        StudentResponse response = studentMapper.toResponse(saved);

        response.setSkills(studentSkillService.getAllByStudent(userId));

        return response;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public VisibilityResponse updateVisibility(UUID userId, boolean isPublic) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        student.setPublicProfile(isPublic);
        Student saved = studentRepository.save(student);

        return VisibilityResponse.builder()
                .studentId(saved.getId())
                .publicProfile(saved.isPublicProfile())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public StudentResponse getPublicProfile(UUID viewerUserId, UUID targetUserId) {

        Student student = studentRepository.findByUserIdWithAllRelations(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        boolean isOwner = student.getUserId().equals(viewerUserId);

        if (!student.isPublicProfile() && !isOwner) {
            throw new AppException(ErrorCode.PROFILE_NOT_PUBLIC);
        }

        StudentResponse response = studentMapper.toResponse(student);
        response.setSkills(studentSkillService.getAllByStudent(targetUserId));

        return response;
    }

    @Override
    public void autoCreateProfile(UUID userId, String fullName) {

        if (studentRepository.existsByUserId(userId)) {
            return;
        }

        Student student = Student.builder()
                .userId(userId)
                .fullName(fullName)
                .publicProfile(false)
                .build();

        studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFullNameByUserId(UUID userId) {

        return studentRepository.findByUserId(userId)
                .map(Student::getFullName)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
    }
}
