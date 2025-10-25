package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyResponse create(UUID creatorUserId, CompanyRequest request); //employer dau tien tao cong ty
    CompanyResponse update(UUID companyId, CompanyRequest request, UUID actorUserId);
    CompanyResponse getById(UUID companyId);
    List<CompanyResponse> getAll();
    void delete(UUID companyId, UUID actorUserId);
}
