package com.backend.matching_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchingResponse {

    private Long cvId;
    private int totalJobs;

    private List<JobMatchResult> results;
}
