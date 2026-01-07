package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.EmployerUpdateRequest;
import com.backend.profileservice.dto.request.JoinCompanyRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v1/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmployerResponse>> getMyProfile(
            @AuthenticationPrincipal String userIdHeader
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        EmployerResponse response = employerService.getByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_FETCHED.getCode(),
                        SuccessCode.EMPLOYER_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    //Public profile để mọi user đã đăng nhập xem employer khác
    @GetMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<EmployerResponse>> getEmployerPublicProfile(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("targetUserId") UUID targetUserId
    ) {
        UUID viewerUserId = UUID.fromString(userIdHeader);
        EmployerResponse response = employerService.getPublicProfile(viewerUserId, targetUserId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_FETCHED.getCode(),
                        SuccessCode.EMPLOYER_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    // Update profile info (name/gender/position)
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<EmployerResponse>> updateMyProfile(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody EmployerUpdateRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        EmployerResponse response = employerService.updateMyProfile(userId, request);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_UPDATED.getCode(),
                        SuccessCode.EMPLOYER_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    // Join existing company
    @PatchMapping("/me/company")
    public ResponseEntity<ApiResponse<EmployerResponse>> joinCompany(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody JoinCompanyRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        EmployerResponse response = employerService.joinCompany(userId, request.getCompanyId());

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_UPDATED.getCode(),
                        SuccessCode.EMPLOYER_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    // Create company and auto join (admin=true)
    @PostMapping("/me/company")
    public ResponseEntity<ApiResponse<EmployerResponse>> createCompanyAndJoin(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody CompanyCreateRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        EmployerResponse response = employerService.createCompanyAndJoin(userId, request);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_UPDATED.getCode(),
                        SuccessCode.EMPLOYER_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    // Leave company (detach)
    @DeleteMapping("/me/company")
    public ResponseEntity<ApiResponse<Void>> leaveCompany(@AuthenticationPrincipal String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        employerService.leaveCompany(userId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_DELETED.getCode(),
                        SuccessCode.EMPLOYER_INFO_DELETED.getMessage(),
                        null
                ));
    }

    @GetMapping("/by-company")
    public ResponseEntity<ApiResponse<List<EmployerResponse>>> getEmployersByCompany(
            @AuthenticationPrincipal String userIdHeader,
            @RequestParam("companyId") UUID companyId
    ) {
        UUID callerUserId = UUID.fromString(userIdHeader);
        List<EmployerResponse> responses = employerService.getAllByCompany(callerUserId, companyId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYERS_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYERS_LIST_FETCHED.getCode(),
                        SuccessCode.EMPLOYERS_LIST_FETCHED.getMessage(),
                        responses
                ));
    }

    // internal endpoint called by auth-service
    @PostMapping("/auto-create")
    public ResponseEntity<ApiResponse<Void>> autoCreateEmployerProfile(@RequestBody Map<String, Object> payload) {
        UUID userId = UUID.fromString((String) payload.get("userId"));
        String fullName = (String) payload.get("fullName");

        employerService.autoCreateProfile(userId, fullName);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_PROFILE_AUTO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_PROFILE_AUTO_CREATED.getCode(),
                        SuccessCode.EMPLOYER_PROFILE_AUTO_CREATED.getMessage(),
                        null
                ));
    }
}
