package com.backend.jobservice.client;

import com.backend.jobservice.dto.response.ApiResponse;
import com.backend.jobservice.dto.response.SkillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "skill-service", url = "${services.skill.base-url}")
public interface SkillClient {

    @GetMapping("/api/skill/v1/skills/{id}")
    ApiResponse<SkillResponse> getSkillById(@PathVariable("id") UUID id);

    @PostMapping("/api/skill/v1/skills/batch")
    ApiResponse<List<SkillResponse>> getSkillsBatch(@RequestBody List<UUID> ids);
}
