package com.backend.jobservice.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobNormUpdatedEvent {

    private UUID internshipPostId;
    private UUID companyId;

    private List<String> skillsNorm;
    private List<String> experienceAreas;
    private List<String> experienceTitles;

    private String educationLevel;
    private List<String> educationMajors;

    private Integer minYears;

    private String workMode;
    private Double locationLat;
    private Double locationLon;

    private OffsetDateTime updatedAt;
}
