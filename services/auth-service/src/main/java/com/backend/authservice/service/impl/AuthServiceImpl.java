package com.backend.authservice.service.impl;

import com.backend.authservice.dto.request.LoginRequest;
import com.backend.authservice.dto.request.LogoutRequest;
import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.Role;
import com.backend.authservice.repository.RefreshTokenRepository;
import com.backend.authservice.repository.UserAccountRepository;
import com.backend.authservice.security.JwtService;
import com.backend.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final long refreshTokenDays = 7; // refresh token sống 7 ngày

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userAccountRepository.existsByEmailIgnoreCase(request.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = (request.getRole() != null) ? request.getRole() : Role.STUDENT;

        UserAccount user = UserAccount.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(AccountStatus.ACTIVE)
                .build();

        user = userAccountRepository.save(user);
        return issueTokens(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getStatus() != AccountStatus.ACTIVE) {
                throw new BadCredentialsException("Account not active");
            }

            return issueTokens(user);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private AuthResponse issueTokens(UserAccount user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken, "Bearer", 15 * 60);
    }
}
