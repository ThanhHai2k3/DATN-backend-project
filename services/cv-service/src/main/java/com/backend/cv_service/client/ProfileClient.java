package com.backend.cv_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "profile-service", url = "${services.profile.base-url}")
public interface ProfileClient {

    @PutMapping("/api/profile/internal/students/{userId}/cv-url")
    void updateCvUrl(@PathVariable("userId") UUID userId, @RequestBody String cvUrl);
}