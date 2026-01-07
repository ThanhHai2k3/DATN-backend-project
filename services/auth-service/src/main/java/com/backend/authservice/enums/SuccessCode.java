package com.backend.authservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    REGISTER_SUCCESS("AUTH_200", "User registered successfully", HttpStatus.OK),
    LOGIN_SUCCESS("AUTH_201", "Login successfully", HttpStatus.OK),
    LOGOUT_SUCCESS("AUTH_202", "Logout successfully", HttpStatus.OK),
    REFRESH_TOKEN_SUCCESS("AUTH_203", "Token refreshed successfully", HttpStatus.OK),
    CHANGE_PASSWORD_SUCCESS("AUTH_204", "Change password successfully", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
