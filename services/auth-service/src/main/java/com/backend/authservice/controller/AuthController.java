package com.backend.authservice.controller;

import com.backend.authservice.dto.request.*;
import com.backend.authservice.dto.response.ApiResponse;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.enums.SuccessCode;
import com.backend.authservice.service.AuthService;
import com.backend.authservice.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request){
        AuthResponse result = authService.register(request);
        return ResponseEntity
                .status(SuccessCode.REGISTER_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.REGISTER_SUCCESS.getCode(),
                        SuccessCode.REGISTER_SUCCESS.getMessage(),
                        result
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse result = authService.login(request);
        return ResponseEntity
                .status(SuccessCode.LOGIN_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.LOGIN_SUCCESS.getCode(),
                        SuccessCode.LOGIN_SUCCESS.getMessage(),
                        result
                ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity
                .status(SuccessCode.CHANGE_PASSWORD_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CHANGE_PASSWORD_SUCCESS.getCode(),
                        SuccessCode.CHANGE_PASSWORD_SUCCESS.getMessage(),
                        null
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity
                .status(SuccessCode.LOGOUT_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.LOGOUT_SUCCESS.getCode(),
                        SuccessCode.LOGOUT_SUCCESS.getMessage(),
                        null
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request){
        AuthResponse result = tokenService.refreshTokens(request.getRefreshToken());

        return ResponseEntity
                .status(SuccessCode.REFRESH_TOKEN_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.REFRESH_TOKEN_SUCCESS.getCode(),
                        "Refresh token successfully",
                        result
                ));
    }
}
