package com.backend.message_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageServiceApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .directory("services/message-service")
                .ignoreIfMissing()
                .load();


        if (dotenv.entries().isEmpty()) {
            dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
        }

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            System.out.println("Loaded env var: " + entry.getKey());
        });

        SpringApplication.run(MessageServiceApplication.class, args);
    }
}
