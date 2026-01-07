package com.backend.jobservice.controller;

import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.response.ApiResponse;
import com.backend.jobservice.dto.response.InternshipPostResponse;
import com.backend.jobservice.dto.response.InternshipPostSummaryResponse;
import com.backend.jobservice.enums.SuccessCode;
import com.backend.jobservice.service.InternshipPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/internship-post")
@RequiredArgsConstructor
@Slf4j
public class InternshipPostController {

    private final InternshipPostService internshipPostService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> createPost(
            @AuthenticationPrincipal String employerId,
            @RequestBody InternshipPostRequest request) {
        UUID employerUUID = UUID.fromString(employerId);
        InternshipPostResponse response = internshipPostService.createPost(employerUUID, request);
        log.info("Created internship post by employerId={}, postId={}", employerUUID, response.getId());

        return ResponseEntity
                .status(SuccessCode.POST_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_CREATED.getCode(),
                        SuccessCode.POST_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> updatePost(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId,
            @RequestBody InternshipPostUpdateRequest request) {
        UUID employerUUID = UUID.fromString(employerId);
        InternshipPostResponse response = internshipPostService.updatePost(employerUUID, postId, request);
        log.info("Updated internship post postId={} by employerId={}", postId, employerUUID);

        return ResponseEntity
                .status(SuccessCode.POST_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_UPDATED.getCode(),
                        SuccessCode.POST_UPDATED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/hide")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hidePost(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId) {
        UUID employerUUID = UUID.fromString(employerId);
        internshipPostService.hidePost(employerUUID, postId);
        log.info("Hidden internship post postId={} by employerId={}", postId, employerUUID);

        return ResponseEntity
                .status(SuccessCode.POST_HIDDEN.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_HIDDEN.getCode(),
                        SuccessCode.POST_HIDDEN.getMessage(),
                        null
                ));
    }


    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<List<InternshipPostSummaryResponse>>> getPendingPosts() {
        List<InternshipPostSummaryResponse> result = internshipPostService.getPendingPosts();
        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }

    @GetMapping("/admin/detail")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> getPostDetailForAdmin(
            @RequestParam("postId") UUID postId) {

        InternshipPostResponse response = internshipPostService.getPostDetailForAdmin(postId);

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/approve")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> approvePost(
            @AuthenticationPrincipal String adminId,
            @RequestParam("postId") UUID postId) {
        UUID adminUUID = UUID.fromString(adminId);
        InternshipPostResponse response = internshipPostService.approvePost(postId, adminUUID);
        log.info("Admin {} approved post {}", adminUUID, postId);

        return ResponseEntity
                .status(SuccessCode.POST_APPROVED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_APPROVED.getCode(),
                        SuccessCode.POST_APPROVED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/reject")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectPost(
            @AuthenticationPrincipal String adminId,
            @RequestParam("postId") UUID postId) {
        internshipPostService.rejectPost(postId);
        log.info("Admin {} rejected post {}", adminId, postId);

        return ResponseEntity
                .status(SuccessCode.POST_HIDDEN.getStatus())
                .body(ApiResponse.success(
                        "POST_REJECTED",
                        "Bài đăng đã bị từ chối phê duyệt",
                        null
                ));
    }


    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> getPostDetail(
            @RequestParam("postId") UUID postId) {
        InternshipPostResponse response = internshipPostService.getPostDetail(postId);

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        response
                ));
    }

    @GetMapping("/employer/detail")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> getEmployerPostDetail(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId
    ) {
        UUID empId = UUID.fromString(employerId);
        InternshipPostResponse response = internshipPostService.getEmployerPostDetail(empId, postId);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                response
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<InternshipPostSummaryResponse>>> searchPosts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "workMode", required = false) String workMode,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "skillId", required = false) UUID skillId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<InternshipPostSummaryResponse> result = internshipPostService.searchPosts(
                keyword,
                workMode,
                skillId,
                companyId,
                location,
                pageable
        );

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }

    @GetMapping("/employer/my-posts")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Page<InternshipPostResponse>>> getMyPosts(
                                                                                 @AuthenticationPrincipal String userIdStr,
                                                                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        UUID employerId = UUID.fromString(userIdStr);

        Page<InternshipPostResponse> response = internshipPostService.getMyPosts(employerId, page, size);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                "Get my posts successfully",
                response
        ));
    }
}