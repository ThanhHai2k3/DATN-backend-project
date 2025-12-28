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

    // tổng điểm matching (0 → 1)
    private double score;

    // --- skill matching ---
    private double skillScore;
    private List<String> matchedSkills;

    // --- location matching ---
    private Double distanceKm;
    private double locationScore;
}
