package com.backend.matching_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Cho phép internal event đi vào không cần login
                        .requestMatchers("/internal/events/**").permitAll()

                        // (tuỳ chọn) cho phép actuator health nếu bạn có dùng
                        .requestMatchers("/actuator/**").permitAll()

                        // còn lại thì chặn / yêu cầu auth
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
