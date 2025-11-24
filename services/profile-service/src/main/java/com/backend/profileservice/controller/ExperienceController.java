package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.student.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.ExperienceResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    private UUID getFakeUserId() {
        // TODO: dùng JWT sau
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    // POST /api/students/me/experiences
    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceResponse>> create(@RequestBody ExperienceCreateRequest request) {
        UUID userId = getFakeUserId();
        ExperienceResponse response = experienceService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_CREATED.getCode(),
                        SuccessCode.EXPERIENCE_CREATED.getMessage(),
                        response
                ));
    }

    // PUT /api/students/me/experiences/{experienceId}
    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(@PathVariable("experienceId") UUID experienceId,
                                                                  @RequestBody ExperienceUpdateRequest request
    ) {
        UUID userId = getFakeUserId();
        ExperienceResponse response = experienceService.update(userId, experienceId, request);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_UPDATED.getCode(),
                        SuccessCode.EXPERIENCE_UPDATED.getMessage(),
                        response
                ));
    }

    // GET /api/students/me/experiences
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getAll() {
        UUID userId = getFakeUserId();
        List<ExperienceResponse> list = experienceService.getAllByStudent(userId);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_FETCHED.getCode(),
                        SuccessCode.EXPERIENCE_FETCHED.getMessage(),
                        list
                ));
    }

    // DELETE /api/students/me/experiences/{experienceId}
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("experienceId") UUID experienceId) {
        UUID userId = getFakeUserId();
        experienceService.delete(userId, experienceId);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_DELETED.getCode(),
                        SuccessCode.EXPERIENCE_DELETED.getMessage(),
                        null
                ));
    }
}
