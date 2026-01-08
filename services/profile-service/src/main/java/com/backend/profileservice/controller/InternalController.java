package com.backend.profileservice.controller;

import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.CompanyBasicResponse;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.CompanyService;
import com.backend.profileservice.service.EmployerService;
import com.backend.profileservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/internal")
@RequiredArgsConstructor
public class InternalController {

    private final StudentService studentService;
    private final EmployerService employerService;
    private final CompanyService companyService;

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

    @GetMapping("/employers/{userId}/full-name")
    public ResponseEntity<ApiResponse<String>> getEmployerFullName(
            @PathVariable("userId") UUID userId
    ) {
        String fullName = employerService.getFullNameByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "EMPLOYER_FULLNAME_FETCHED",
                        "Employer full name fetched successfully",
                        fullName
                )
        );
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

    @GetMapping("/employers/me/company-id")
    public ResponseEntity<ApiResponse<UUID>> getMyCompanyId(
            @AuthenticationPrincipal String userIdHeader
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        UUID companyId = employerService.getMyCompanyId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "EMPLOYER_COMPANY_ID_FETCHED",
                        "Employer companyId fetched successfully",
                        companyId
                )
        );
    }

    @GetMapping("/companies/batch")
    public ResponseEntity<ApiResponse<List<CompanyBasicResponse>>> getCompaniesBatch(
            @RequestParam("ids") List<UUID> ids
    ) {
        List<CompanyBasicResponse> responses = companyService.getBasicBatch(ids);

        return ResponseEntity
                .status(SuccessCode.COMPANIES_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANIES_LIST_FETCHED.getCode(),
                        "Companies fetched successfully",
                        responses
                ));
    }
}
