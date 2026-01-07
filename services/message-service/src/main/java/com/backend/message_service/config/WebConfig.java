package com.backend.message_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**") // Áp dụng cho tất cả các đường dẫn bắt đầu bằng /api/
//                .allowedOrigins("*") // Cho phép tất cả các origin. Để an toàn hơn, bạn có thể thay "*" bằng "http://localhost:3000" nếu client chạy ở cổng 3000
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Các phương thức được phép
//                .allowedHeaders("*") // Cho phép tất cả các header
//                .allowCredentials(false)
//                .maxAge(3600);
//    }
}