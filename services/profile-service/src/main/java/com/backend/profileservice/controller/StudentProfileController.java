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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v1/student-profile")
@RequiredArgsConstructor
@Slf4j
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(@RequestParam("userId") UUID userId){
        StudentProfileResponse response = studentProfileService.getByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.PROFILE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_FETCHED.getCode(),
                        SuccessCode.PROFILE_FETCHED.getMessage(),
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
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateVisibility(@RequestParam("userId") UUID userId,
                                                                                @RequestParam("visible") boolean visible) {

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
    public ResponseEntity<ApiResponse<Void>> replaceEducations(@RequestParam("userId") UUID userId, @Valid @RequestBody List<EducationDTO> educations) {
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
    public ResponseEntity<ApiResponse<Void>> replaceExperiences(@RequestParam("userId") UUID userId, @Valid @RequestBody List<ExperienceDTO> experiences) {
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
    public ResponseEntity<ApiResponse<StudentProfileResponse>> replaceSkills(@RequestParam("userId") UUID userId, @Valid @RequestBody List<StudentSkillDTO> skills) {
        StudentProfileResponse response = studentProfileService.replaceSkills(userId, skills);
        return ResponseEntity
                .status(SuccessCode.SKILLS_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILLS_UPDATED.getCode(),
                        SuccessCode.SKILLS_UPDATED.getMessage(),
                        response
                ));
    }

    @PostMapping("/auto-create")
    public ResponseEntity<ApiResponse<Void>> autoCreateProfile(@RequestBody Map<String, Object> payload) {
        UUID userId = UUID.fromString((String) payload.get("userId"));
        String fullName = (String) payload.get("fullName");

        studentProfileService.autoCreateProfile(userId, fullName);
        log.info("Auto-created profile for userId={} with fullName='{}'", userId, fullName);

        return ResponseEntity
                .status(SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getCode(),
                        SuccessCode.STUDENT_PROFILE_AUTO_CREATED.getMessage(),
                        null
                ));
    }

}
