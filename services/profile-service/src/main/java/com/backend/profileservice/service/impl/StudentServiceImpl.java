package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.StudentCreateRequest;
import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.entity.*;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.StudentMapper;
import com.backend.profileservice.repository.*;
import com.backend.profileservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getByUserId(UUID userId){
        Student profile = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentMapper.toResponse(profile);
    }

    @Override
    public StudentResponse updateProfile(UUID userId, StudentUpdateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        studentMapper.updateEntity(student, request);

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Override
    public StudentResponse updateVisibility(UUID userId, boolean isPublic) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        student.setPublicProfile(isPublic);

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
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
}
