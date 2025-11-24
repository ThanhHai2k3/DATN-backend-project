package com.backend.profileservice.dto.external.skill;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    private String name;
    private String description;
}
