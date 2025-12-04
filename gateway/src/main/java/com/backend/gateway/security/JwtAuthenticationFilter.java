package com.backend.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtils jwtUtils;

    // Những route không cần JWT
    private static final List<String> WHITELIST = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error",
            "/favicon.ico"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){

        String path = exchange.getRequest().getURI().getPath();
        log.info("➡️ Incoming request: {}", path);

        // 1. Nếu request nằm trong whitelist -> bỏ qua JWT check
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 2. Lấy header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("➡️ Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // 3. Validate token
        if (!jwtUtils.isTokenValid(token)) {
            return unauthorized(exchange, "Invalid or expired JWT token");
        }

        // 4. Extract userId & role từ token
        UUID userId;
        String role;

        try {
            userId = jwtUtils.extractUserId(token);
            role = jwtUtils.extractRole(token);
        } catch (Exception e) {
            log.error("JWT parse error: {}", e.getMessage());
            return unauthorized(exchange, "Token parse error");
        }

        // 5. Thêm vào header để các service phía sau dùng
        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId.toString())
                .header("X-User-Role", role)
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(modifiedExchange);
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.warn("Unauthorized request: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
