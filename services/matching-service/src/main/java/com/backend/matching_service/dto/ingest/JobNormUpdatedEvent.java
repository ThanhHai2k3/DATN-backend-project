package com.backend.matching_service.dto.ingest;

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
public class JobNormUpdatedEvent {

    private UUID internshipPostId;
    private UUID companyId;

    // normalized fields
    private List<String> skillsNorm;
    private List<String> experienceAreas;
    private List<String> experienceTitles;

    private String educationLevel;
    private List<String> educationMajors;

    private BigDecimal minYears;

    private String workMode;
    private Double locationLat;
    private Double locationLon;

    // metadata
    private OffsetDateTime updatedAt;
}
