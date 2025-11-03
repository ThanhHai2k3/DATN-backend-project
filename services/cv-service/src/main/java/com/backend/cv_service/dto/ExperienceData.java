package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ExperienceData {
    private String position;

    // private long durationInMonths; 
}
