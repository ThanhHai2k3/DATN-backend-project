package com.backend.profileservice.controller;

import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/internal")
@RequiredArgsConstructor
public class InternalController {

    private final StudentService studentService;

    @GetMapping("/students/{userId}/full-name")
    public ResponseEntity<ApiResponse<String>> getStudentFullName(
            @PathVariable("userId") UUID userId
    ) {
        String fullName = studentService.getFullNameByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "STUDENT_FULLNAME_FETCHED",
                        "Student full name fetched successfully",
                        fullName
                )
        );
    }

    @PutMapping("/students/{userId}/cv-url")
    public ResponseEntity<ApiResponse<Void>> updateCvUrl(
            @PathVariable("userId") UUID userId,
            @RequestBody String cvUrl
    ) {
        studentService.updateCvUrl(userId, cvUrl);
        return ResponseEntity.ok(ApiResponse.success("CV_URL_UPDATED", "Updated CV URL successfully", null));
    }

    @PostMapping("/students/batch-info")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsBatch(
            @RequestBody List<UUID> userIds
    ) {
        List<StudentResponse> students = studentService.getBasicInfoBatch(userIds);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "BATCH_FETCH_SUCCESS",
                        "Fetched student batch info successfully",
                        students
                )
        );
    }
}
