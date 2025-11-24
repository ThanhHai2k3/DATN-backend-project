package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.student.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.student.ExperienceResponse;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {

    ExperienceResponse create(UUID userId, ExperienceCreateRequest request);

    ExperienceResponse update(UUID userId, UUID experienceId, ExperienceUpdateRequest request);

    void delete(UUID userId, UUID experienceId);

    List<ExperienceResponse> getAllByStudent(UUID userId);
}
