package com.backend.profileservice.controller;

import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
