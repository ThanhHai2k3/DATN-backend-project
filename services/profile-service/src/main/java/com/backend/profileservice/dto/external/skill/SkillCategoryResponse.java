package com.backend.profileservice.dto.external.skill;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillCategoryResponse {

    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
