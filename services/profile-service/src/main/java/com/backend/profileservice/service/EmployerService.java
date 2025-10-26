package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.response.EmployerResponse;

import java.util.List;
import java.util.UUID;

public interface EmployerService {
    EmployerResponse getByUserId(UUID userId);
    EmployerResponse createOrJoinCompany(UUID userId, EmployerRequest request);
    EmployerResponse updateProfile(UUID userId, EmployerRequest request);
    List<EmployerResponse> getAllByCompany(UUID companyId);
    void delete(UUID userId);
}
