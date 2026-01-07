package com.backend.matching_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchingRequest {

    private Long cvId;

    private Double desiredLat;
    private Double desiredLon;

    private Double maxDistanceKm;
}
