package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillResponse {

    private UUID id;
    private UUID skillId;
    private String skillName;     // optional: fetched from skill-service
    private String category;      // optional
    private String level;
    private Integer years;
    private String note;
}
