package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.StudentCreateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.StudentService;
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
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> getProfile(@RequestParam("userId") UUID userId){
        StudentResponse response = studentService.getByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.PROFILE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_FETCHED.getCode(),
                        SuccessCode.PROFILE_FETCHED.getMessage(),
                        response
                ));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> upsertProfile(@Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentService.upsert(request);
        return ResponseEntity
                .status(SuccessCode.PROFILE_UPSERTED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPSERTED.getCode(),
                        SuccessCode.PROFILE_UPSERTED.getMessage(),
                        response
                ));
    }

    @PutMapping("/me/visibility")
    public ResponseEntity<ApiResponse<StudentResponse>> updateVisibility(@RequestParam("userId") UUID userId,
                                                                         @RequestParam("visible") boolean visible) {

        StudentResponse response = studentService.updateVisibility(userId, visible);
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
        studentService.replaceEducations(userId, educations);
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
        studentService.replaceExperiences(userId, experiences);
        return ResponseEntity
                .status(SuccessCode.EXP_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXP_UPDATED.getCode(),
                        SuccessCode.EXP_UPDATED.getMessage(),
                        null
                ));
    }

    @PutMapping("/me/skills")
    public ResponseEntity<ApiResponse<StudentResponse>> replaceSkills(@RequestParam("userId") UUID userId, @Valid @RequestBody List<StudentSkillDTO> skills) {
        StudentResponse response = studentService.replaceSkills(userId, skills);
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

        studentService.autoCreateProfile(userId, fullName);
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
