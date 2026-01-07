package com.backend.authservice.enums;

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
    INVALID_REFRESH_TOKEN("AUTH_007", "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_ACTION_ON_ADMIN("AUTH_008", "Action not allowed on System Admin account", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("AUTH_009", "Unauthorized", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
