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
                .status(SuccessCode.COMPANY_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_FETCHED.getCode(),
                        SuccessCode.COMPANY_FETCHED.getMessage(),
                        response
                ));
    }

    // ======================
    // 2️⃣ Tạo công ty mới (khi employer đầu tiên chưa có company)
    // ======================
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestParam("userId") UUID userId,
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.create(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_CREATED.getCode(),
                        SuccessCode.COMPANY_CREATED.getMessage(),
                        response
                ));
    }

    // ======================
    // 3️⃣ Cập nhật thông tin công ty của user
    // ======================
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @RequestParam("userId") UUID userId,
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.updateByUser(userId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_UPDATED.getCode(),
                        SuccessCode.COMPANY_UPDATED.getMessage(),
                        response
                ));
    }

    // ======================
    // 4️⃣ Lấy danh sách tất cả công ty (dành cho admin / debug)
    // ======================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        List<CompanyResponse> responses = companyService.getAll();
        return ResponseEntity
                .status(SuccessCode.COMPANY_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_LIST_FETCHED.getCode(),
                        SuccessCode.COMPANY_LIST_FETCHED.getMessage(),
                        responses
                ));
    }

    // ======================
    // 5️⃣ Xoá công ty của user (nếu user là admin)
    // ======================
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@RequestParam("userId") UUID userId) {
        companyService.deleteByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_DELETED.getCode(),
                        SuccessCode.COMPANY_DELETED.getMessage(),
                        null
                ));
    }
}
