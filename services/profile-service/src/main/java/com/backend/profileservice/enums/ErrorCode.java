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

    STUDENT_NOT_FOUND("STU_001", "Student with userId not found", HttpStatus.NOT_FOUND),
    EDUCATION_NOT_FOUND("STU_002","Education not found", HttpStatus.NOT_FOUND),
    EXPERIENCE_NOT_FOUND("STU_003", "Experience not found", HttpStatus.NOT_FOUND),
    PROJECT_NOT_FOUND("STU_004", "Project not found", HttpStatus.NOT_FOUND),
    SOCIAL_LINK_NOT_FOUND("STU_005", "Social link not found", HttpStatus.NOT_FOUND),
    STUDENT_SKILL_EXISTS("STU_006", "Student already have this skill", HttpStatus.BAD_REQUEST),
    STUDENT_SKILL_NOT_FOUND("STU_007", "Student don't have that skill", HttpStatus.BAD_REQUEST),
    PROFILE_NOT_PUBLIC("STU_008", "Profile is private", HttpStatus.FORBIDDEN),

    SKILL_NOT_FOUND("SK_001", "Skill not found", HttpStatus.NOT_FOUND),
    CATEGORY_REQUIRED("SK_002", "Category required for new skill", HttpStatus.BAD_REQUEST),
    SKILL_ID_OR_NAME_REQUIRED("SK_003", "Either skillId or skillName must be provided", HttpStatus.BAD_REQUEST),

    FORBIDDEN("STU_", "Access denied", HttpStatus.FORBIDDEN),

    EMPLOYER_NOT_FOUND("EMP_001", "Employer with userId not found", HttpStatus.NOT_FOUND),
    EMPLOYER_PROFILE_EXISTED("EMP_002", "Employer profile already exists", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_ADMIN_EMPLOYER("EMP_003", "Cannot delete employer admin in company", HttpStatus.FORBIDDEN),
    EMPLOYER_ALREADY_HAS_COMPANY("EMP_004", "Employer already belongs to a company", HttpStatus.BAD_REQUEST),
    EMPLOYER_HAS_NO_COMPANY("EMP_005", "Employer has not joined any company yet", HttpStatus.BAD_REQUEST),

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
