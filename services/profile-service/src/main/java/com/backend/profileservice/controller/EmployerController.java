package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.EmployerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employers")
@RequiredArgsConstructor
@Slf4j
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/me")
    public ResponseEntity<ApiResponse<EmployerResponse>> createOrJoinCompany(@RequestParam("userId")UUID userId,
                                                                             @Valid @RequestBody EmployerRequest request){

        EmployerResponse response = employerService.createOrJoinCompany(userId, request);
        log.info("Created or joined company for userId={}, employerId={}", userId, response.getId());

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_CREATED.getCode(),
                        SuccessCode.EMPLOYER_INFO_CREATED.getMessage(),
                        response
                ));
    }

    //Lấy thông tin Employer theo userId
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmployerResponse>> getByUserId(@RequestParam("userId") UUID userId){
        EmployerResponse response = employerService.getByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_FETCHED.getCode(),
                        SuccessCode.EMPLOYER_INFO_FETCHED.getMessage(),
                        response
                ));
    }

    //Cập nhật thông tin Employer
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<EmployerResponse>> updateEmployer(@RequestParam("userId") UUID userId,
                                                                        @Valid @RequestBody EmployerRequest request) {

        EmployerResponse response = employerService.updateProfile(userId, request);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_UPDATED.getCode(),
                        SuccessCode.EMPLOYER_INFO_UPDATED.getMessage(),
                        response
                ));
    }

    // Xóa Employer profile (rời khỏi công ty)
    // Employer không bị xóa user account; chỉ unlink company
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteEmployer(@RequestParam("userId") UUID userId) {
        employerService.delete(userId);
        log.info("Deleted employer profile (detached company) for userId={}", userId);

        return ResponseEntity
                .status(SuccessCode.EMPLOYER_INFO_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYER_INFO_DELETED.getCode(),
                        SuccessCode.EMPLOYER_INFO_DELETED.getMessage(),
                        null
                ));
    }

    // Lấy danh sách Employer trong 1 công ty (Dành cho admin công ty hoặc HR)
    @GetMapping("/by-company")
    public ResponseEntity<ApiResponse<List<EmployerResponse>>> getEmployersByCompany(@RequestParam("companyId") UUID companyId) {
        List<EmployerResponse> responses = employerService.getAllByCompany(companyId);
        return ResponseEntity
                .status(SuccessCode.EMPLOYERS_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EMPLOYERS_LIST_FETCHED.getCode(),
                        SuccessCode.EMPLOYERS_LIST_FETCHED.getMessage(),
                        responses
                ));
    }
}
