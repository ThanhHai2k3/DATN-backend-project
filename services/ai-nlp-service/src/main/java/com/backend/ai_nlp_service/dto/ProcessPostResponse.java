package com.backend.ai_nlp_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPostResponse {

    private String postId;

    // skills
    private List<NormalizedSkill> skillsNorm;

    private BigDecimal experienceYearsMin;
    private BigDecimal experienceYearsMax;
    private String experienceLevel; // INTERN, JUNIOR, ...

    private List<String> domains;

    private List<String> locationsNorm;
    private List<String> workModesNorm;

    private BigDecimal durationMonthsMin;
    private BigDecimal durationMonthsMax;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private String salaryType;

    private String modelVersion;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NormalizedSkill {
        private String skillKey;    // "java"
        private String canonical;   // "Java"
        private String importance;  // "MUST" / "NICE"
    }
}
