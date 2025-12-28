package com.backend.applyingservice.client;

import com.backend.applyingservice.dto.external.CvDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cv-service", url = "${services.cv.base-url}")
public interface CvClient {
    @GetMapping("/api/cv/v1/{cvId}")
    CvDetailDto getCvById(@PathVariable("cvId") Long cvId,
                          @RequestHeader("X-User-Id") String studentId,
                          @RequestHeader("X-User-Role") String role);
}