package com.backend.matching_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchingRequest {

    // CV mà user chọn để match
    private Long cvId;

    // vị trí mong muốn (override vị trí trong CV nếu có)
    private Double desiredLat;
    private Double desiredLon;

    // optional: giới hạn bán kính tìm kiếm
    private Double maxDistanceKm;
}
