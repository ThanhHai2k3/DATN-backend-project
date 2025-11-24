package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.student.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.student.EducationResponse;

import java.util.List;
import java.util.UUID;

public interface EducationService {

    EducationResponse create(UUID userId, EducationCreateRequest request);

    EducationResponse update(UUID userId, UUID educationId, EducationUpdateRequest request);

    void delete(UUID userId, UUID educationId);

    List<EducationResponse> getAllByStudent(UUID userId);
}
