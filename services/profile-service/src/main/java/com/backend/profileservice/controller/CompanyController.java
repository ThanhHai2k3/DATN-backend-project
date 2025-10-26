package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@RequestParam("userId") UUID userId) {
        CompanyResponse response = companyService.getByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_FETCHED.getCode(),
                        SuccessCode.COMPANY_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    // Tạo công ty mới (khi employer đầu tiên chưa có company)
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestParam("userId") UUID userId,
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.create(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_CREATED.getCode(),
                        SuccessCode.COMPANY_INFO_CREATED.getMessage(),
                        response
                ));
    }

    // Cập nhật thông tin công ty của user
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @RequestParam("userId") UUID userId,
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.updateByUser(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_UPDATED.getCode(),
                        SuccessCode.COMPANY_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    // Lấy danh sách tất cả công ty (dành cho admin / debug)
    @GetMapping("/all")
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

    // Xoá công ty của user (nếu user là admin)
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@RequestParam("userId") UUID userId) {
        companyService.deleteByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_INFO_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_INFO_DELETED.getCode(),
                        SuccessCode.COMPANY_INFO_DELETED.getMessage(),
                        null
                ));
    }
}
