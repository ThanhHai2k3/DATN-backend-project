package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class EducationDto {
    private String universityName;
    private String major;
    private BigDecimal originalGpa;
    private Integer gpaScale;
}