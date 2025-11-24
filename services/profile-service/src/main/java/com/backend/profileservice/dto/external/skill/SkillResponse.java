package com.backend.profileservice.dto.external.skill;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {

    private UUID id;
    private String name;
    private String description;
    private SkillCategoryResponse category;
    private Instant createdAt;
    private Instant updatedAt;
}
