package com.backend.applyingservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // --- System Errors (Copy từ Job Service) ---
    VALIDATION_FAILED("SYS_400", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_UUID("SYS_401", "Invalid UUID format", HttpStatus.BAD_REQUEST),
    INVALID_ENUM("SYS_402", "Invalid enum value", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE("SYS_403", "Upstream service unavailable", HttpStatus.BAD_GATEWAY),
    INTERNAL_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("401", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("403", "FORBIDDEN", HttpStatus.FORBIDDEN),

    // --- Applying Service Specific Errors ---
    APPLICATION_NOT_FOUND("APP_001", "Application not found", HttpStatus.NOT_FOUND),
    DUPLICATE_APPLICATION("APP_002", "You have already applied to this job", HttpStatus.BAD_REQUEST),
    JOB_POST_NOT_ACTIVE("APP_003", "Job post is not active", HttpStatus.BAD_REQUEST),
    CV_NOT_FOUND("APP_004", "CV not found or access denied", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("APP_005", "You do not have permission to view this application", HttpStatus.FORBIDDEN),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}