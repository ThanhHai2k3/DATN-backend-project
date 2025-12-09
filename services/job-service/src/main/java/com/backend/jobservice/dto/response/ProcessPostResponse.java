package com.backend.jobservice.dto.response;

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

    // ===== Skills chuẩn hoá =====
    private List<String> skillsNorm;      // Ví dụ ["java", "spring_boot", "rest_api"]

    // ===== Experience =====
    private BigDecimal experienceYearsMin;
    private BigDecimal experienceYearsMax;
    private String experienceLevel;

    // ===== Domain nghề nghiệp =====
    private List<String> domains;

    // ===== Location & Work mode =====
    private List<String> locationsNorm;
    private List<String> workModesNorm;

    // ===== Duration =====
    private BigDecimal durationMonthsMin;
    private BigDecimal durationMonthsMax;

    // ===== Salary =====
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;       // VND / USD / …
    private String salaryType;           // per_month / fixed / range …

    // location
    private Double lat;
    private Double lon;

    // ===== Meta =====
    private String modelVersion;
    private Instant processedAt;
}
