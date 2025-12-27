package com.backend.cv_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvNormUpdatedEvent {

    private Long cvId;
    private UUID studentId;

    private List<String> skillsNorm;
    private List<String> experienceAreas;
    private List<String> experienceTitles;

    private String educationLevel;
    private List<String> educationMajors;

    private BigDecimal yearsTotal;

    private String modelVersion;
    private OffsetDateTime updatedAt;
}
