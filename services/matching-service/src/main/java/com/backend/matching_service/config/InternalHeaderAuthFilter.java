package com.backend.matching_service.config;

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

        String userIdHeader = request.getHeader("X-User-Id");

        // 1) lấy authorities theo chuẩn mới
        String authoritiesHeader = request.getHeader("X-Authorities");

        // 2) fallback: nếu gateway chỉ gửi 1 role
        String roleHeader = request.getHeader("X-User-Role"); // ví dụ "STUDENT"

        log.info(">>> Headers received:");
        log.info("X-User-Id = {}", userIdHeader);
        log.info("X-Authorities = {}", authoritiesHeader);
        log.info("X-User-Role = {}", roleHeader);

        // nếu đã có Authentication rồi thì khỏi set lại
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Không có userId -> không tạo Authentication
        if (userIdHeader == null || userIdHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Fallback logic: nếu authorities không có thì dùng role
        if ((authoritiesHeader == null || authoritiesHeader.isBlank())
                && roleHeader != null && !roleHeader.isBlank()) {
            authoritiesHeader = roleHeader; // "STUDENT"
        }

        UUID userId;
        try {
            userId = UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException e) {
            // userId không hợp lệ -> bỏ qua
            filterChain.doFilter(request, response);
            return;
        }

        List<SimpleGrantedAuthority> authorities = parseAuthorities(authoritiesHeader);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId,        // principal = UUID
                null,
                authorities    // ["STUDENT"] hoặc ["STUDENT","EMPLOYER"]
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info(">>> Authentication CREATED: principal={}, authorities={}",
                auth.getPrincipal(), auth.getAuthorities());

        filterChain.doFilter(request, response);
    }


    private List<SimpleGrantedAuthority> parseAuthorities(String header) {
        if (header == null || header.isBlank()) return List.of();
        return Arrays.stream(header.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new) // "STUDENT"
                .toList();
    }
}
