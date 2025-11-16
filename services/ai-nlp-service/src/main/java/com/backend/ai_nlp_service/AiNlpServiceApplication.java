package com.backend.ai_nlp_service;

import com.backend.ai_nlp_service.service.SkillExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class AiNlpServiceApplication {

	public static void main(String[] args) {
        System.out.println("oi thoi chettt");
        List<String> skills = SkillExtractor.extractSkills(
                "Experienced with Java, Spring Boot and ReactJS"
        );

        System.out.println(skills);

        SpringApplication.run(AiNlpServiceApplication.class, args);
	}

}
