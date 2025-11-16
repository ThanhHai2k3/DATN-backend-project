package com.backend.skillservice.service;

import com.backend.skillservice.dto.request.CreateCategoryRequest;
import com.backend.skillservice.dto.request.UpdateCategoryRequest;
import com.backend.skillservice.dto.response.SkillCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface SkillCategoryService {
    SkillCategoryResponse createCategory(CreateCategoryRequest request);
    SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    void deleteCategory(UUID id);
    SkillCategoryResponse getCategory(UUID id);
    List<SkillCategoryResponse> getAllCategories();
}
