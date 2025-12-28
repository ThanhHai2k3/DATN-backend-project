package com.backend.applyingservice.client;

import com.backend.applyingservice.dto.external.ApiResponse;
import com.backend.applyingservice.dto.external.InternshipPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "job-service", url = "${services.job.base-url}")
public interface JobClient {
    @GetMapping("/api/internship-post/detail")
    ApiResponse<InternshipPostResponse> getPostDetail(@RequestParam("postId") UUID postId);
}