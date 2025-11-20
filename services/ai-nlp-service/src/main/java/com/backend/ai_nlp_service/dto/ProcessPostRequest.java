package com.backend.ai_nlp_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPostRequest {
    private String postId;      // uuid string
    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private String workMode;    // ONSITE / REMOTE / HYBRID
    private String salary;
}
