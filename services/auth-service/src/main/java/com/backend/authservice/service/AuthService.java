package com.backend.authservice.service;

import com.backend.authservice.dto.request.ChangePasswordRequest;
import com.backend.authservice.dto.request.LoginRequest;
import com.backend.authservice.dto.request.LogoutRequest;
import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(LogoutRequest request);

    void changePassword(ChangePasswordRequest request);
}
