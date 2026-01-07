package com.backend.matching_service.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchResult {

    private UUID internshipPostId;
    private UUID companyId;

    private double score;

    private double skillScore;
    private List<String> matchedSkills;

    private Double distanceKm;
    private double locationScore;
}
