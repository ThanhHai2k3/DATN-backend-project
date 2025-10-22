package com.backend.profileservice.controller;

import com.backend.profileservice.dto.EducationDTO;
import com.backend.profileservice.dto.ExperienceDTO;
import com.backend.profileservice.dto.StudentSkillDTO;
import com.backend.profileservice.dto.request.StudentProfileRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.StudentProfileResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/student-profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(@RequestParam UUID userId){
        StudentProfileResponse response = studentProfileService.getByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.PROFILE_UPSERTED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPSERTED.getCode(),
                        SuccessCode.PROFILE_UPSERTED.getMessage(),
                        response
                ));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> upsertProfile(@Valid @RequestBody StudentProfileRequest request) {
        StudentProfileResponse response = studentProfileService.upsert(request);
        return ResponseEntity
                .status(SuccessCode.PROFILE_UPSERTED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPSERTED.getCode(),
                        SuccessCode.PROFILE_UPSERTED.getMessage(),
                        response
                ));
    }

    @PutMapping("/me/visibility")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateVisibility(@RequestParam UUID userId,
                                                                                @RequestParam boolean visible) {

        StudentProfileResponse response = studentProfileService.updateVisibility(userId, visible);
        return ResponseEntity
                .status(SuccessCode.PROFILE_UPSERTED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPSERTED.getCode(),
                        SuccessCode.PROFILE_UPSERTED.getMessage(),
                        response
                ));
    }

    @PutMapping("/me/educations")
    public ResponseEntity<ApiResponse<Void>> replaceEducations(@RequestParam UUID userId, @Valid @RequestBody List<EducationDTO> educations) {
        studentProfileService.replaceEducations(userId, educations);
        return ResponseEntity
                .status(SuccessCode.EDU_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDU_UPDATED.getCode(),
                        SuccessCode.EDU_UPDATED.getMessage(),
                        null
                ));
    }

    @PutMapping("/me/experiences")
    public ResponseEntity<ApiResponse<Void>> replaceExperiences(@RequestParam UUID userId, @Valid @RequestBody List<ExperienceDTO> experiences) {
        studentProfileService.replaceExperiences(userId, experiences);
        return ResponseEntity
                .status(SuccessCode.EXP_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXP_UPDATED.getCode(),
                        SuccessCode.EXP_UPDATED.getMessage(),
                        null
                ));
    }

    @PutMapping("/me/skills")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> replaceSkills(@RequestParam UUID userId, @Valid @RequestBody List<StudentSkillDTO> skills) {
        StudentProfileResponse response = studentProfileService.replaceSkills(userId, skills);
        return ResponseEntity
                .status(SuccessCode.SKILLS_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILLS_UPDATED.getCode(),
                        SuccessCode.SKILLS_UPDATED.getMessage(),
                        response
                ));
    }
}
