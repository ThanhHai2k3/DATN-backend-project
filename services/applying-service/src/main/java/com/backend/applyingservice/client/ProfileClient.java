package com.backend.applyingservice.client;

import com.backend.applyingservice.dto.external.ApiResponse;
import com.backend.applyingservice.dto.external.StudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "profile-service", url = "${services.profile.base-url}")
public interface ProfileClient {
    @GetMapping("/api/profile/v2/students/{userId}")
    ApiResponse<StudentResponse> getStudentProfile(
            @RequestHeader("X-User-Id") String viewerId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable("userId") UUID userId);

    @GetMapping("/api/profile/internal/students/{userId}/full-name")
    ApiResponse<String> getStudentFullName(@PathVariable("userId") UUID userId);

    @PostMapping("/api/profile/internal/students/batch-info")
    ApiResponse<List<StudentResponse>> getStudentsBatch(@RequestBody List<UUID> userIds);
}