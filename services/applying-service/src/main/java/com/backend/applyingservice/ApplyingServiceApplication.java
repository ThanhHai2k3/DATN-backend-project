package com.backend.applyingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.backend.applyingservice.client")
public class ApplyingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplyingServiceApplication.class, args);
	}

}
