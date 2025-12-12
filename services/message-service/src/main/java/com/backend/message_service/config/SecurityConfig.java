package com.backend.message_service.config; // đổi theo package của bạn

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // ✅ Bắt buộc để @PreAuthorize hoạt động
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InternalHeaderAuthFilter internalHeaderAuthFilter) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                // Bước 1 chỉ cần vậy, tạm cho qua hết để bạn test @PreAuthorize trước
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(internalHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
