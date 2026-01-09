package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.dto.response.student.VisibilityResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    StudentResponse getByUserId(UUID userId);

    StudentResponse updateProfile(UUID userId, StudentUpdateRequest request);

    VisibilityResponse updateVisibility(UUID userId, boolean isPublic);

    StudentResponse getPublicProfile(UUID viewerUserId, UUID targetUserId);

    void autoCreateProfile(UUID userId, String fullName);

    String getFullNameByUserId(UUID userId);

    List<StudentResponse> getBasicInfoBatch(List<UUID> userIds);

    void updateCvUrl(UUID userId, String cvUrl);

    StudentResponse updateAvatarUrl(UUID userId, String avatarUrl);
}
