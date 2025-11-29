package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.dto.response.student.VisibilityResponse;

import java.util.UUID;

public interface StudentService {

    StudentResponse getByUserId(UUID userId);

    StudentResponse updateProfile(UUID userId, StudentUpdateRequest request);

    VisibilityResponse updateVisibility(UUID userId, boolean isPublic);

    void autoCreateProfile(UUID userId, String fullName);
}
