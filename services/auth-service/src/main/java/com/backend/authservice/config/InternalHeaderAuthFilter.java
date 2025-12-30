package com.backend.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class InternalHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Debug nhanh: xem auth header có bị forward không (không bắt buộc)
        // System.out.println("AUTH HEADER = " + request.getHeader("Authorization"));

        String userIdHeader = request.getHeader("X-User-Id");
        String authoritiesHeader = request.getHeader("X-Authorities"); // ví dụ: "SYSTEM_ADMIN" hoặc "ROLE_SYSTEM_ADMIN"
        String roleHeader = request.getHeader("X-User-Role");          // ví dụ: "SYSTEM_ADMIN"

        log.info(">>> [InternalHeaderAuthFilter] Headers received: X-User-Id={}, X-Authorities={}, X-User-Role={}",
                userIdHeader, authoritiesHeader, roleHeader);

        // Chỉ xử lý khi request có X-User-Id (tức là request nội bộ đi qua gateway).
        // Nếu không có header này -> bỏ qua để endpoint public như /login, /register hoạt động bình thường.
        if (userIdHeader == null || userIdHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Fallback: nếu gateway không gửi X-Authorities thì dùng X-User-Role
        String effectiveAuthorities = authoritiesHeader;
        if ((effectiveAuthorities == null || effectiveAuthorities.isBlank())
                && roleHeader != null && !roleHeader.isBlank()) {
            effectiveAuthorities = roleHeader;
        }

        UUID userId;
        try {
            userId = UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException e) {
            log.warn(">>> [InternalHeaderAuthFilter] Invalid X-User-Id: {}", userIdHeader);
            filterChain.doFilter(request, response);
            return;
        }

        // Log auth hiện có (nếu có) để debug case bị JWT filter set trước
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing != null) {
            log.warn(">>> [InternalHeaderAuthFilter] Existing authentication found -> will OVERRIDE. existingAuthorities={}",
                    existing.getAuthorities());
        }

        List<SimpleGrantedAuthority> authorities = parseAuthorities(effectiveAuthorities);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId,     // principal = UUID
                null,
                authorities // ví dụ: [SYSTEM_ADMIN]
        );

        // ✅ Override: luôn set theo internal headers khi có X-User-Id
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info(">>> [InternalHeaderAuthFilter] Authentication SET: principal={}, authorities={}",
                auth.getPrincipal(), auth.getAuthorities());

        filterChain.doFilter(request, response);
    }

    /**
     * Convert header -> GrantedAuthorities
     * - Hỗ trợ format: "SYSTEM_ADMIN" hoặc "ROLE_SYSTEM_ADMIN"
     * - Hỗ trợ nhiều quyền: "SYSTEM_ADMIN,STUDENT"
     *
     * Vì bạn dùng hasAuthority('SYSTEM_ADMIN'), ta chuẩn hóa về "SYSTEM_ADMIN".
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String header) {
        if (header == null || header.isBlank()) return List.of();

        return Arrays.stream(header.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith("ROLE_") ? s.substring("ROLE_".length()) : s) // strip ROLE_ nếu có
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
