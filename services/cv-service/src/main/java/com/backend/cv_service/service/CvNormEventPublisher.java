package com.backend.cv_service.service;

import com.backend.cv_service.dto.CvNormUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CvNormEventPublisher {
    private final RestTemplate restTemplate;

    // URL matching-service
    private static final String MATCHING_URL =
            "http://localhost:8088/internal/events/cv-norm-updated";

    public void publish(CvNormUpdatedEvent event) {
        restTemplate.postForEntity(
                MATCHING_URL,
                event,
                Void.class
        );
    }
}
