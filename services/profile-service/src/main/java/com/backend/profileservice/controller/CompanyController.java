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

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@RequestParam("creatorUserId") UUID creatorUserId,
                                                                      @Valid @RequestBody CompanyRequest request){
        CompanyResponse response = companyService.create(creatorUserId, request);
        return ResponseEntity
                .status(SuccessCode.COMPANY_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_CREATED.getCode(),
                        SuccessCode.COMPANY_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID companyId,
            @Valid @RequestBody CompanyRequest request,
            @RequestParam("actorUserId") UUID actorUserId
    ) {
        CompanyResponse response = companyService.update(companyId, request, actorUserId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_UPDATED.getCode(),
                        SuccessCode.COMPANY_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable UUID companyId) {
        CompanyResponse response = companyService.getById(companyId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_FETCHED.getCode(),
                        SuccessCode.COMPANY_FETCHED.getMessage(),
                        response
                ));
    }

    @GetMapping
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

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable UUID companyId,
            @RequestParam("actorUserId") UUID actorUserId
    ) {
        companyService.delete(companyId, actorUserId);
        return ResponseEntity
                .status(SuccessCode.COMPANY_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANY_DELETED.getCode(),
                        SuccessCode.COMPANY_DELETED.getMessage(),
                        null
                ));
    }
}
