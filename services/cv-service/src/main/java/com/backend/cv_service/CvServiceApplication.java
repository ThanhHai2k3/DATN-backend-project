package com.backend.cv_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CvServiceApplication {

	public static void main(String[] args) {
		System.out.println("Duke say hiii!!");
        Dotenv dotenv = Dotenv.configure()
                .directory("services/cv-service")
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
        SpringApplication.run(CvServiceApplication.class, args);
	}

}
