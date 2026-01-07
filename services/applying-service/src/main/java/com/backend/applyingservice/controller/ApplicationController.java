package com.backend.applyingservice.controller;

import com.backend.applyingservice.dto.external.ApiResponse;
import com.backend.applyingservice.dto.request.ApplyRequest;
import com.backend.applyingservice.dto.request.UpdateApplicationStatusRequest;
import com.backend.applyingservice.dto.response.ApplicationResponse;
import com.backend.applyingservice.enums.SuccessCode;
import com.backend.applyingservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.backend.applyingservice.dto.response.EmployerDashboardStatsDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applying/v1")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/stats")
    public ResponseEntity<ApiResponse<EmployerDashboardStatsDto>> getDashboardStats(
            @RequestBody List<UUID> jobIds
    ) {
        EmployerDashboardStatsDto stats = applicationService.getStatsForEmployer(jobIds);

        return ResponseEntity.ok(ApiResponse.success(
                "STATS_FETCHED",
                "Lấy thống kê dashboard thành công",
                stats
        ));
    }

    // 1. Sinh viên nộp đơn
    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyJob(
            @RequestBody ApplyRequest request,
            @AuthenticationPrincipal String studentIdStr) {

        UUID studentId = UUID.fromString(studentIdStr);
        ApplicationResponse response = applicationService.applyJob(studentId, request);

        return ResponseEntity
                .status(SuccessCode.APPLICATION_SUBMITTED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.APPLICATION_SUBMITTED.getCode(),
                        SuccessCode.APPLICATION_SUBMITTED.getMessage(),
                        response
                ));
    }

    // 2. Sinh viên xem lịch sử nộp đơn
    @GetMapping("/student/my-applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal String studentIdStr) {

        UUID studentId = UUID.fromString(studentIdStr);
        List<ApplicationResponse> response = applicationService.getMyApplications(studentId);

        return ResponseEntity
                .status(SuccessCode.APPLICATION_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.APPLICATION_LIST_FETCHED.getCode(),
                        SuccessCode.APPLICATION_LIST_FETCHED.getMessage(),
                        response
                ));
    }

    // 3. Nhà tuyển dụng xem chi tiết đơn
    @GetMapping("/employer/applications/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetail(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal String employerIdStr) {

        UUID employerId = UUID.fromString(employerIdStr);
        ApplicationResponse response = applicationService.getApplicationDetailForEmployer(employerId, id);

        return ResponseEntity
                .status(SuccessCode.APPLICATION_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.APPLICATION_FETCHED.getCode(),
                        SuccessCode.APPLICATION_FETCHED.getMessage(),
                        response
                ));
    }

    @GetMapping("/employer/posts/{postId}/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getCandidatesByPost(
            @PathVariable("postId") UUID postId,

            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            // -----------------

            @AuthenticationPrincipal String employerIdStr) {

        UUID employerId = UUID.fromString(employerIdStr);

        Page<ApplicationResponse> response = applicationService.getApplicationsByPostId(employerId, postId, page, size);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.APPLICATION_LIST_FETCHED.getCode(),
                "Get candidate list successfully",
                response
        ));
    }

    // 4. Nhà tuyển dụng cập nhật trạng thái đơn (Duyệt / Từ chối)
    @PatchMapping("/employer/applications/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable("id") UUID applicationId,
            @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal String employerIdStr) {

        UUID employerId = UUID.fromString(employerIdStr);

        applicationService.updateApplicationStatus(
                employerId,
                applicationId,
                request.getStatus(),
                request.getNote()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "APP_UPDATED",
                "Application status updated to " + request.getStatus(),
                null
        ));
    }
}