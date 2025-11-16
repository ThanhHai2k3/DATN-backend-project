package com.backend.skillservice.controller;

import com.backend.skillservice.dto.request.CreateSkillRequest;
import com.backend.skillservice.dto.request.UpdateSkillRequest;
import com.backend.skillservice.dto.response.ApiResponse;
import com.backend.skillservice.dto.response.SkillResponse;
import com.backend.skillservice.enums.SuccessCode;
import com.backend.skillservice.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@RequestBody @Valid CreateSkillRequest request) {
        SkillResponse result = skillService.createSkill(request);
        return ResponseEntity
                .status(SuccessCode.SKILL_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILL_CREATED.getCode(),
                        SuccessCode.SKILL_CREATED.getMessage(),
                        result
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(@PathVariable UUID id, @RequestBody @Valid UpdateSkillRequest request) {
        SkillResponse result = skillService.updateSkill(id, request);
        return ResponseEntity
                .status(SuccessCode.SKILL_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILL_UPDATED.getCode(),
                        SuccessCode.SKILL_UPDATED.getMessage(),
                        result
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkill(@PathVariable UUID id) {
        SkillResponse result = skillService.getSkill(id);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> searchSkills(@RequestParam("q") String keyword) {
        List<SkillResponse> result = skillService.searchSkills(keyword);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkillsByCategory(@PathVariable UUID categoryId) {
        List<SkillResponse> result = skillService.getSkillsByCategory(categoryId);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        null
                ));
    }
}
