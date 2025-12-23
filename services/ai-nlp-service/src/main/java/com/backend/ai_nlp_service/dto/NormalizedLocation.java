package com.backend.ai_nlp_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NormalizedLocation {

    private String rawLocation;   // location user nhập
    private String displayName;   // Mapbox trả về
    private Double lat;
    private Double lon;
}
