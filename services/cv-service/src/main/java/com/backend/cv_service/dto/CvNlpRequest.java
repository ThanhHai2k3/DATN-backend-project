package com.backend.cv_service.dto;

import lombok.Data;

@Data
public class CvNlpRequest {
    private Long cvId;
    private String rawText;
}
