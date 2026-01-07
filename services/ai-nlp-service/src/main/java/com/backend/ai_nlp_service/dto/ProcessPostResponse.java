package com.backend.ai_nlp_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPostResponse {

    private String postId;

    private List<String> skillsNorm;

    private BigDecimal experienceYearsMin;
    private BigDecimal experienceYearsMax;
    private String experienceLevel;

    private List<String> domains;

    private List<String> locationsNorm;
    private List<String> workModesNorm;

    private BigDecimal durationMonthsMin;
    private BigDecimal durationMonthsMax;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private String salaryType;

    private Double lat;
    private Double lon;

    private String modelVersion;
    private Instant processedAt;
}