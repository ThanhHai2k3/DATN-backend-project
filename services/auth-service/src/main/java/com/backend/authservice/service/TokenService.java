package com.backend.authservice.service;

import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.UserAccount;

public interface TokenService {

    AuthResponse issueTokens(UserAccount userAccount);

    AuthResponse refreshTokens(String refreshToken);
}
