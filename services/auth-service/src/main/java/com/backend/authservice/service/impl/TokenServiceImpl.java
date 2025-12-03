package com.backend.authservice.service.impl;

import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.mapper.AuthMapper;
import com.backend.authservice.repository.RefreshTokenRepository;
import com.backend.authservice.security.JwtService;
import com.backend.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthMapper authMapper;
    private static final long REFRESH_TOKEN_DAYS = 7;
    private static final long ACCESS_TOKEN_SECONDS = 15 * 60; //15p

    @Override
    public AuthResponse issueTokens(UserAccount userAccount){

        //Tính thời gian hiện tại và thời điểm hết hạn access token
        long nowEpochMillis = Instant.now().toEpochMilli();
        long expiresAtEpochMillis = nowEpochMillis + ACCESS_TOKEN_SECONDS * 1000;

        String accessToken = jwtService.generateToken(userAccount);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = RefreshToken.builder()
                .user(userAccount)
                .token(refreshToken)
                .expiresAt(Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(token);

        AuthResponse response = authMapper.toAuthResponse(userAccount);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresInSeconds(ACCESS_TOKEN_SECONDS);
        response.setIssuedAt(nowEpochMillis);
        response.setExpiresAt(expiresAtEpochMillis);

        return response;
    }

    @Override
    @Transactional
    public AuthResponse refreshTokens(String refreshTokens){
        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokens)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        //Kiểm tra đã revoke chưa hoặc đã hết hạn chưa
        if(stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())){
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //Revoke refresh token cũ (token rotation)
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        //Lấy user tương ứng
        UserAccount user = stored.getUser();

        //Cấp phát cặp accessToken + refreshToken mới
        return issueTokens(user);
    }
}
