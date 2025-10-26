package com.backend.gateway.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource; // <-- Đúng cho WebFlux
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**").permitAll()
                        .anyExchange().permitAll()                 // tạm thời mở toàn bộ cho dễ test
                )
                .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép nguồn gốc Frontend của bạn
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // <--- KHẮC PHỤC        // Cho phép các phương thức (POST, GET, PUT, DELETE...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cho phép gửi các Header tiêu chuẩn và Authorization header
        configuration.setAllowedHeaders(Arrays.asList("*")); // <--- KHẮC PHỤC        // Cho phép gửi cookies và credentials (nếu cần)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng CORS cho mọi đường dẫn
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
