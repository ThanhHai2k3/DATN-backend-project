package com.backend.skillservice.service;

import com.backend.skillservice.dto.request.CreateSkillRequest;
import com.backend.skillservice.dto.request.UpdateSkillRequest;
import com.backend.skillservice.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {
    SkillResponse createSkill(CreateSkillRequest request);
    SkillResponse updateSkill(UUID id, UpdateSkillRequest request);
    void deleteSkill(UUID id);
    SkillResponse getSkill(UUID id);
    List<SkillResponse> searchSkills(String keyword);
    List<SkillResponse> getSkillsByCategory(UUID categoryId);

    List<SkillResponse> getSkillsByIds(List<UUID> ids);

}
