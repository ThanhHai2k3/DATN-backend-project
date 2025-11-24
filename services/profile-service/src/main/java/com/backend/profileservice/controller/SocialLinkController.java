package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.student.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.SocialLinkResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.SocialLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me/social-links")
@RequiredArgsConstructor
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    private UUID getFakeUserId() {
        // TODO: dùng JWT sau
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    // POST /api/students/me/social-links
    @PostMapping
    public ResponseEntity<ApiResponse<SocialLinkResponse>> create(@RequestBody SocialLinkCreateRequest request) {
        UUID userId = getFakeUserId();
        SocialLinkResponse response = socialLinkService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_CREATED.getCode(),
                        SuccessCode.SOCIAL_CREATED.getMessage(),
                        response
                ));
    }

    // PUT /api/students/me/social-links/{linkId}
    @PutMapping("/{linkId}")
    public ResponseEntity<ApiResponse<SocialLinkResponse>> update(@PathVariable("linkId") UUID linkId,
                                                                  @RequestBody SocialLinkUpdateRequest request
    ) {
        UUID userId = getFakeUserId();
        SocialLinkResponse response = socialLinkService.update(userId, linkId, request);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_UPDATED.getCode(),
                        SuccessCode.SOCIAL_UPDATED.getMessage(),
                        response
                ));
    }

    // GET /api/students/me/social-links
    @GetMapping
    public ResponseEntity<ApiResponse<List<SocialLinkResponse>>> getAll() {
        UUID userId = getFakeUserId();
        List<SocialLinkResponse> list = socialLinkService.getAllByStudent(userId);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_FETCHED.getCode(),
                        SuccessCode.SOCIAL_FETCHED.getMessage(),
                        list
                ));
    }

    // DELETE /api/students/me/social-links/{linkId}
    @DeleteMapping("/{linkId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("linkId") UUID linkId) {
        UUID userId = getFakeUserId();
        socialLinkService.delete(userId, linkId);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_DELETED.getCode(),
                        SuccessCode.SOCIAL_DELETED.getMessage(),
                        null
                ));
    }
}
