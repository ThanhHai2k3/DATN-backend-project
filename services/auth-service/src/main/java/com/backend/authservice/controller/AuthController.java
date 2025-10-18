package com.backend.authservice.controller;

import com.backend.authservice.dto.request.LoginRequest;
import com.backend.authservice.dto.request.LogoutRequest;
import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.ApiResponse;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.enums.SuccessCode;
import com.backend.authservice.service.AuthService;
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
}
