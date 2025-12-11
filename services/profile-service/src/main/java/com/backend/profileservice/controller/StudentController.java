package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.dto.response.student.VisibilityResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    // GET /api/students/me
    // SELF PROFILE
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> getMyProfile(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        StudentResponse response = studentService.getByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.PROFILE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_FETCHED.getCode(),
                        SuccessCode.PROFILE_FETCHED.getMessage(),
                        response
                ));
    }

    // PUBLIC PROFILE
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<StudentResponse>> getPublicProfile(
            @AuthenticationPrincipal String viewerIdHeader,
            @PathVariable("userId") UUID targetUserId
    ) {
        UUID viewerUserId = UUID.fromString(viewerIdHeader);

        StudentResponse profile = studentService.getPublicProfile(viewerUserId, targetUserId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "PROFILE_PUBLIC_FETCHED",
                        "Public student profile fetched successfully",
                        profile
                )
        );
    }

    // PUT /api/students/me
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> updateMyProfile(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody StudentUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        StudentResponse response = studentService.updateProfile(userId, request);

        return ResponseEntity
                .status(SuccessCode.PROFILE_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPDATED.getCode(),
                        SuccessCode.PROFILE_UPDATED.getMessage(),
                        response
                ));
    }

    // PATCH /api/students/me/visibility
    @PatchMapping("/me/visibility")
    public ResponseEntity<ApiResponse<VisibilityResponse>> updateVisibility(
            @AuthenticationPrincipal String userIdHeader,
            @RequestParam("public") boolean isPublic)
    {
        UUID userId = UUID.fromString(userIdHeader);
        VisibilityResponse response = studentService.updateVisibility(userId, isPublic);

        return ResponseEntity
                .status(SuccessCode.PROFILE_VISIBILITY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_VISIBILITY_UPDATED.getCode(),
                        SuccessCode.PROFILE_VISIBILITY_UPDATED.getMessage(),
                        response
                ));
    }

    // AUTO CREATE PROFILE (Called by Auth-Service)
    // POST /api/students/auto-create
    @PostMapping("/auto-create")
    public ResponseEntity<ApiResponse<Void>> autoCreateProfile(@RequestBody Map<String, Object> payload) {

        UUID userId = UUID.fromString((String) payload.get("userId"));
        String fullName = (String) payload.get("fullName");

        studentService.autoCreateProfile(userId, fullName);
        log.info("Auto-created profile for userId={} with fullName='{}'", userId, fullName);

        return ResponseEntity
                .status(SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getCode(),
                        SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getMessage(),
                        null
                ));
    }

}
