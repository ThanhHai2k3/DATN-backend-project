package com.backend.profileservice.client;

import com.backend.profileservice.dto.external.skill.CreateSkillRequest;
import com.backend.profileservice.dto.external.skill.SkillResponse;
import com.backend.profileservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "skill-service",url = "${services.skill.base-url}")
public interface SkillClient {

    @GetMapping("/api/skill/v1/skills/{id}")
    ApiResponse<SkillResponse> getSkillById(@PathVariable("id") UUID id);

    @GetMapping("/api/skill/v1/skills/search")
    ApiResponse<SkillResponse> getSkillByName(@RequestParam("name") String name);

    @PostMapping("/api/skill/v1/skills")
    ApiResponse<SkillResponse> createSkill(@RequestBody CreateSkillRequest request);
}
