package com.backend.authservice.service.impl;

import com.backend.authservice.dto.request.LoginRequest;
import com.backend.authservice.dto.request.LogoutRequest;
import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.enums.Role;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.mapper.AuthMapper;
import com.backend.authservice.repository.RefreshTokenRepository;
import com.backend.authservice.repository.UserAccountRepository;
import com.backend.authservice.service.AuthService;
import com.backend.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.UUID;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    private String getFullNameFromProfile(UUID userId) {
        try {
            String url = "http://localhost:8082/api/profile/v2/students/me";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> apiResponse = response.getBody();
                if (apiResponse.containsKey("data")) {
                    Map<String, Object> apiData = (Map<String, Object>) apiResponse.get("data");
                    if (apiData != null && apiData.containsKey("fullName") && apiData.get("fullName") != null) {
                        return (String) apiData.get("fullName");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch full name for user {}: {}", userId, e.getMessage());
        }
        return "User";
    }


    @Override
    public AuthResponse register(RegisterRequest request){
        if(userAccountRepository.existsByEmailIgnoreCase(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserAccount user = authMapper.toUserAccountEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userAccountRepository.save(user);

        try {
            // Chuẩn bị payload body JSON
            Map<String, Object> body = new HashMap<>();
            body.put("userId", user.getId());
            body.put("fullName", request.getFullName());

            String baseUrl = "http://localhost:8082/api/profile";
            String url;

            if (user.getRole() == Role.STUDENT) {
                url = baseUrl + "/v2/students/auto-create";
            } else if (user.getRole() == Role.EMPLOYER) {
                url = baseUrl + "/v1/employers/auto-create";
            } else {
                url = null;
            }

            if (url != null) {
                restTemplate.postForEntity(url, body, Void.class);
                log.info("Auto-created profile for userId={} with role={}", user.getId(), user.getRole());
            } else {
                log.warn("Role {} does not have profile auto-create configured.", user.getRole());
            }
        } catch (Exception e) {
            log.error("Failed to auto-create profile for userId={} (role={})", user.getId(), user.getRole(), e);
        }

        AuthResponse response = tokenService.issueTokens(user);
        // SỬA: Đặt Full Name từ request (đã có sẵn)
        response.setFullName(request.getFullName());

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (user.getStatus() != AccountStatus.ACTIVE) {
                throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
            }

            AuthResponse response = tokenService.issueTokens(user);

            // THÊM LẠI LOGIC LẤY VÀ SET FULL NAME
            String fullName = getFullNameFromProfile(user.getId());
            response.setFullName(fullName);

            return response;

        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
}
