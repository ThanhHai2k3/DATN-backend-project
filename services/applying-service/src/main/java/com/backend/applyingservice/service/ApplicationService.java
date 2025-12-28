package com.backend.applyingservice.service;

import com.backend.applyingservice.dto.request.ApplyRequest;
import com.backend.applyingservice.dto.response.ApplicationResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    ApplicationResponse applyJob(UUID studentId, ApplyRequest request);
    List<ApplicationResponse> getMyApplications(UUID studentId);
    ApplicationResponse getApplicationDetailForEmployer(UUID employerId, UUID applicationId);
    Page<ApplicationResponse> getApplicationsByPostId(UUID employerId, UUID jobPostId, int page, int size);
}