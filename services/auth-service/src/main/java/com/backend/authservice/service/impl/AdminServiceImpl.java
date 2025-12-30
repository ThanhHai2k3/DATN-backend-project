package com.backend.authservice.service.impl;

import com.backend.authservice.dto.admin.UpdateUserRoleRequest;
import com.backend.authservice.dto.admin.UpdateUserStatusRequest;
import com.backend.authservice.dto.admin.AdminUserDetail;
import com.backend.authservice.dto.admin.AdminUserSummary;
import com.backend.authservice.dto.admin.PageResponse;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.repository.RefreshTokenRepository;
import com.backend.authservice.repository.UserAccountRepository;
import com.backend.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummary> listUsers(String q, String role, String status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<UserAccount> p = userAccountRepository.search(q, role, status, pageable);

        return PageResponse.<AdminUserSummary>builder()
                .items(p.getContent().stream().map(this::toSummary).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalItems(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetail getUserDetail(UUID userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        int tokenCount = refreshTokenRepository.countByUser_Id(userId);
        return toDetail(user, tokenCount);
    }

    @Override
    @Transactional
    public AdminUserDetail updateUserStatus(UUID userId, UpdateUserStatusRequest req) {
        if (req.getStatus() == null) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(req.getStatus());
        UserAccount saved = userAccountRepository.save(user);

        int tokenCount = refreshTokenRepository.countByUser_Id(userId);
        return toDetail(saved, tokenCount);
    }

    @Override
    @Transactional
    public AdminUserDetail updateUserRole(UUID userId, UpdateUserRoleRequest req) {
        if (req.getRole() == null) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setRole(req.getRole());
        UserAccount saved = userAccountRepository.save(user);

        int tokenCount = refreshTokenRepository.countByUser_Id(userId);
        return toDetail(saved, tokenCount);
    }

    private AdminUserSummary toSummary(UserAccount u) {
        return AdminUserSummary.builder()
                .id(u.getId())
                .email(u.getEmail())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    // ✅ không truy cập u.getRefreshTokens() nữa
    private AdminUserDetail toDetail(UserAccount u, int tokenCount) {
        return AdminUserDetail.builder()
                .id(u.getId())
                .email(u.getEmail())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .refreshTokenCount(tokenCount)
                .build();
    }
}
