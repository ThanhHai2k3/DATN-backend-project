package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.student.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.EducationResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    //Fake userId for testing before JWT integration
//    private UUID getFakeUserId() {
//        // TODO: dùng JWT sau
//        return UUID.fromString("11111111-1111-1111-1111-111111111111");
//    }
    private UUID getUserIdFromHeader(String userIdHeader) {
        return UUID.fromString(userIdHeader);
    }

    // POST /api/students/me/educations
    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> create(@RequestHeader("X-User-Id") String userIdHeader,
                                                                 @RequestBody EducationCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        EducationResponse response = educationService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_CREATED.getCode(),
                        SuccessCode.EDUCATION_CREATED.getMessage(),
                        response
                ));
    }

    // PUT /api/students/me/educations/{educationId}
    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> update(@RequestHeader("X-User-Id") String userIdHeader,
                                                                 @PathVariable("educationId") UUID educationId,
                                                                 @RequestBody EducationUpdateRequest request
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        EducationResponse response = educationService.update(userId, educationId, request);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_UPDATED.getCode(),
                        SuccessCode.EDUCATION_UPDATED.getMessage(),
                        response
                ));
    }

    // GET /api/students/me/educations
    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getAll(@RequestHeader("X-User-Id") String userIdHeader){
        UUID userId = UUID.fromString(userIdHeader);
        List<EducationResponse> educations = educationService.getAllByStudent(userId);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_FETCHED.getCode(),
                        SuccessCode.EDUCATION_FETCHED.getMessage(),
                        educations
                ));
    }

    // DELETE /api/students/me/educations/{educationId}
    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestHeader("X-User-Id") String userIdHeader,
                                                    @PathVariable("educationId") UUID educationId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        educationService.delete(userId, educationId);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_DELETED.getCode(),
                        SuccessCode.EDUCATION_DELETED.getMessage(),
                        null
                ));
    }
}
