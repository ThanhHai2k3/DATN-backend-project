package com.backend.ai_nlp_service.dto;

import lombok.Data;

@Data
public class ProcessCvRequest {

    private Long cvId;      // id CV trong cv-service
    private String rawText; // text CV sau khi parse

}
