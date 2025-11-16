package com.backend.jobservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_FAILED("SYS_400", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_UUID("SYS_401", "Invalid UUID", HttpStatus.BAD_REQUEST),
    INVALID_ENUM("SYS_402", "Invalid enum value", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE("SYS_403", "Service unavailable", HttpStatus.BAD_GATEWAY),
    INTERNAL_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    POST_NOT_FOUND_OR_FORBIDDEN("JOB_001", "Internship post not found or employer not allowed", HttpStatus.FORBIDDEN),
    POST_EXPIRED("JOB_002", "This post already expired", HttpStatus.UNAUTHORIZED),
    POST_NOT_FOUND("JOB_003", "Internship post with this id not found", HttpStatus.NOT_FOUND),
    POST_ALREADY_HIDDEN("JOB_004", "This internship post already hidden", HttpStatus.BAD_REQUEST),
    HIDE_POST_DENIED("JOB_005", "You are not allowed to modify this post", HttpStatus.FORBIDDEN),

    SKILL_NOT_FOUND("SK_005", "Skill not found", HttpStatus.NOT_FOUND),
    SKILL_ID_REQUIRED("SK_006", "Skill id required", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
