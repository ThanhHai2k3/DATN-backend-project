package com.backend.jobservice.client;

import com.backend.jobservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "profile-service", url = "${services.profile.base-url}")
public interface ProfileClient {

    // job-service phải truyền X-User-Id & X-User-Role để profile-service set Authentication.
    @GetMapping("/api/profile/internal/employers/me/company-id")
    ApiResponse<UUID> getMyCompanyId(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role
    );
}
