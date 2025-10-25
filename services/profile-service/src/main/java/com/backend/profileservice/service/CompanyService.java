package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyResponse create(UUID userId, CompanyRequest request);
    CompanyResponse updateByUser(UUID userId, CompanyRequest request);
    CompanyResponse getByUserId(UUID userId);
    List<CompanyResponse> getAll();
    void deleteByUserId(UUID userId);
}
