package com.backend.skillservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CATEGORY_ALREADY_EXIST("SK_001", "Category name already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("SK_002", "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_HAS_SKILLS("SK_003", "Category has skills, cannot delete", HttpStatus.BAD_REQUEST),

    SKILL_ALREADY_EXIST("SK_004", "Skill name already exists", HttpStatus.BAD_REQUEST),
    SKILL_NOT_FOUND("SK_005", "Skill not found", HttpStatus.NOT_FOUND),

    VALIDATION_FAILED("SYS_400", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_UUID("SYS_401", "Invalid UUID", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
