package com.backend.profileservice.dto.response.student;

import com.backend.profileservice.enums.SkillLevel;
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
    private SkillLevel level;
    private Integer years;
    private String note;
}
