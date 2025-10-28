package com.backend.message_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageServiceApplication {
    public static void main(String[] args) {

        // ✅ Load .env từ cả 2 nơi để chắc chắn
        Dotenv dotenv = Dotenv.configure()
                .directory("services/message-service") // nếu chạy từ root
                .ignoreIfMissing()
                .load();

        // ✅ Nếu không tìm thấy, thử lại ở thư mục root
        if (dotenv.entries().isEmpty()) {
            dotenv = Dotenv.configure()
                    .directory(".") // thư mục hiện tại
                    .ignoreIfMissing()
                    .load();
        }

        // ✅ Inject biến môi trường vào System properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            System.out.println("Loaded env var: " + entry.getKey());
        });

        SpringApplication.run(MessageServiceApplication.class, args);
    }
}
