package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.request.CompanyUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @AuthenticationPrincipal String userIdHeader
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        CompanyResponse response = companyService.getByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_FETCHED.getCode(),
                        SuccessCode.COMPANY_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    // Lấy chi tiết công ty theo companyId (cho mọi user đã đăng nhập)
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(
            @PathVariable("companyId") UUID companyId
    ) {
        CompanyResponse response = companyService.getById(companyId);

        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_FETCHED.getCode(),
                        SuccessCode.COMPANY_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    @PostMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @AuthenticationPrincipal String userIdHeader,
            @Valid @RequestBody CompanyCreateRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        CompanyResponse response = companyService.create(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_CREATED.getCode(),
                        SuccessCode.COMPANY_INFO_CREATED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody CompanyUpdateRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        CompanyResponse response = companyService.updateByUser(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_UPDATED.getCode(),
                        SuccessCode.COMPANY_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@AuthenticationPrincipal String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        companyService.deleteByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_DELETED.getCode(),
                        SuccessCode.COMPANY_INFO_DELETED.getMessage(),
                        null
                ));
    }

    // Lấy danh sách tất cả công ty (dành cho admin / debug)
    @GetMapping()
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        List<CompanyResponse> responses = companyService.getAll();
        return ResponseEntity
                .status(SuccessCode.COMPANIES_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANIES_LIST_FETCHED.getCode(),
                        SuccessCode.COMPANIES_LIST_FETCHED.getMessage(),
                        responses
                ));
    }
}
