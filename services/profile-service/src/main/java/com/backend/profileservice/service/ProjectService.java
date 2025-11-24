package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.student.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.student.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse create(UUID userId, ProjectCreateRequest request);

    ProjectResponse update(UUID userId, UUID projectId, ProjectUpdateRequest request);

    void delete(UUID userId, UUID projectId);

    List<ProjectResponse> getAllByStudent(UUID userId);
}
