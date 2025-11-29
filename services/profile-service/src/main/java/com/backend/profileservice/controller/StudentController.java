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
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    private UUID getFakeUserId() {
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    // GET /api/students/me
    @GetMapping
    public ResponseEntity<ApiResponse<StudentResponse>> getProfile() {
        UUID userId = getFakeUserId();
        StudentResponse response = studentService.getByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.PROFILE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_FETCHED.getCode(),
                        SuccessCode.PROFILE_FETCHED.getMessage(),
                        response
                ));
    }

    // PUT /api/students/me
    @PutMapping
    public ResponseEntity<ApiResponse<StudentResponse>> updateProfile(@RequestBody StudentUpdateRequest request) {
        UUID userId = getFakeUserId();

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
    @PatchMapping("/visibility")
    public ResponseEntity<ApiResponse<VisibilityResponse>> updateVisibility(@RequestParam("public") boolean isPublic) {
        UUID userId = getFakeUserId();

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
    // POST /api/students/me/auto-create
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
