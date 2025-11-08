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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internship-post")
@RequiredArgsConstructor
@Slf4j
public class InternshipPostController {

    private final InternshipPostService internshipPostService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> createPost(@RequestParam("employerId") UUID employerId,
                                                                          @RequestBody InternshipPostRequest request){

        InternshipPostResponse response = internshipPostService.createPost(employerId, request);
        log.info("Created internship post by employerId={}, postId={}", employerId, response.getId());

        return ResponseEntity
                .status(SuccessCode.POST_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_CREATED.getCode(),
                        SuccessCode.POST_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> updatePost(
            @RequestParam("employerId") UUID employerId,
            @RequestParam("postId") UUID postId,
            @RequestBody InternshipPostUpdateRequest request){

        InternshipPostResponse response = internshipPostService.updatePost(employerId, postId, request);
        log.info("Updated internship post postId={} by employerId={}", postId, employerId);

        return ResponseEntity
                .status(SuccessCode.POST_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_UPDATED.getCode(),
                        SuccessCode.POST_UPDATED.getMessage(),
                        response
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

    @PatchMapping("/hide")
    public ResponseEntity<ApiResponse<Void>> hidePost(
            @RequestParam("employerId") UUID employerId,
            @RequestParam("postId") UUID postId) {

        internshipPostService.hidePost(employerId, postId);
        log.info("Hidden internship post postId={} by employerId={}", postId, employerId);

        return ResponseEntity
                .status(SuccessCode.POST_HIDDEN.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_HIDDEN.getCode(),
                        SuccessCode.POST_HIDDEN.getMessage(),
                        null
                ));
    }

    @PatchMapping("/approve")
    public ResponseEntity<ApiResponse<InternshipPostResponse>> approvePost(
            @RequestParam("adminId") UUID adminId,
            @RequestParam("postId") UUID postId) {

        InternshipPostResponse response = internshipPostService.approvePost(postId, adminId);
        log.info("Admin {} approved post {}", adminId, postId);

        return ResponseEntity
                .status(SuccessCode.POST_APPROVED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_APPROVED.getCode(),
                        SuccessCode.POST_APPROVED.getMessage(),
                        response
                ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<InternshipPostSummaryResponse>>> searchPosts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "workMode", required = false) String workMode,
            @RequestParam(value = "skillId", required = false) UUID skillId,
            @RequestParam(value = "companyId", required = false) UUID companyId) {

        List<InternshipPostSummaryResponse> result = internshipPostService.searchPosts(keyword, workMode, skillId, companyId);

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }
}
