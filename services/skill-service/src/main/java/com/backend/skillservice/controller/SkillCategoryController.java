package com.backend.skillservice.controller;

import com.backend.skillservice.dto.request.CreateCategoryRequest;
import com.backend.skillservice.dto.request.UpdateCategoryRequest;
import com.backend.skillservice.dto.response.ApiResponse;
import com.backend.skillservice.dto.response.SkillCategoryResponse;
import com.backend.skillservice.dto.response.SkillResponse;
import com.backend.skillservice.enums.SuccessCode;
import com.backend.skillservice.service.SkillCategoryService;
import com.backend.skillservice.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class SkillCategoryController {

    private final SkillCategoryService categoryService;
    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> createCategory(@RequestBody @Valid CreateCategoryRequest request){
        SkillCategoryResponse result = categoryService.createCategory(request);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_CREATED.getCode(),
                        SuccessCode.CATEGORY_CREATED.getMessage(),
                        result
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> updateCategory(@PathVariable UUID id,
                                                                             @RequestBody @Valid UpdateCategoryRequest request) {

        SkillCategoryResponse result = categoryService.updateCategory(id, request);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_UPDATED.getCode(),
                        SuccessCode.CATEGORY_UPDATED.getMessage(),
                        result
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillCategoryResponse>>> getAllCategories() {

        List<SkillCategoryResponse> result = categoryService.getAllCategories();
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> getCategory(
            @PathVariable UUID id) {

        SkillCategoryResponse result = categoryService.getCategory(id);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));


    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkillsByCategory(@PathVariable UUID id) {
        List<SkillResponse> result = skillService.getSkillsByCategory(id);

        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_DELETED.getCode(),
                        SuccessCode.CATEGORY_DELETED.getMessage(),
                        null
                ));
    }
}
