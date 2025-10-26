package com.backend.profileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS("AUTH_001", "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("AUTH_002", "Email or password invalid", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("AUTH_003", "User account not found", HttpStatus.NOT_FOUND),
    ACCOUNT_INACTIVE("AUTH_004", "Account is not active", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("AUTH_005", "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("AUTH_006", "Invalid token", HttpStatus.UNAUTHORIZED),

    PROFILE_NOT_FOUND("STU_001", "Student profile not found", HttpStatus.NOT_FOUND),

    EMPLOYER_NOT_FOUND("EMP_001", "Employer not found", HttpStatus.NOT_FOUND),
    EMPLOYER_PROFILE_EXISTED("EMP_002", "Employer profile already exists", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_ADMIN_EMPLOYER("EMP_003", "Cannot delete employer admin in company", HttpStatus.FORBIDDEN),

    COMPANY_NAME_EXISTED("PRO_001", "Company name already exists", HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND("PRO_002", "Company not found", HttpStatus.NOT_FOUND),
    UPDATE_COMPANY_DENIED("PRO_003", "You are not allowed to update this company", HttpStatus.FORBIDDEN),

    VALIDATION_FAILED("SYS_400", "Request validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
