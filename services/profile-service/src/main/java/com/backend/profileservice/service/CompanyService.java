package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.request.CompanyUpdateRequest;
import com.backend.profileservice.dto.response.CompanyBasicResponse;
import com.backend.profileservice.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    CompanyResponse create(UUID userId, CompanyCreateRequest request);

    CompanyResponse updateByUser(UUID userId, CompanyUpdateRequest request);

    CompanyResponse getByUserId(UUID userId);

    CompanyResponse getById(UUID companyId);

    List<CompanyResponse> getAll();

    void deleteByUserId(UUID userId);

    List<CompanyBasicResponse> getBasicBatch(List<UUID> ids);
}
