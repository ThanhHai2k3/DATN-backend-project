package com.backend.jobservice.client;

import com.backend.jobservice.dto.response.ApiResponse;
import com.backend.jobservice.dto.response.SkillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "skill-service", url = "${services.skill.base-url}")
public interface SkillClient {

    @GetMapping("/api/skill/v1/skills/{id}")
    ApiResponse<SkillResponse> getSkillById(@PathVariable("id") UUID id);
}
