package com.backend.ai_nlp_service;

import com.backend.ai_nlp_service.service.SkillExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

@SpringBootApplication
public class AiNlpServiceApplication {

	public static void main(String[] args) {
        System.out.println("oi thoi chettt");
        List<String> skills = SkillExtractor.extractSkills(
                "Experienced with Java, Spring Boot and ReactJS"
        );

        System.out.println(skills);
        Dotenv dotenv = Dotenv.configure()
                .directory("services/ai-nlp-service")
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

        SpringApplication.run(AiNlpServiceApplication.class, args);
	}

}
