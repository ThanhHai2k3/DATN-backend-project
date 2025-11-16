package com.backend.skillservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSkillRequest {

    @NotBlank
    private String name;

    @NotNull
    private UUID categoryId;

    private String description;
}
