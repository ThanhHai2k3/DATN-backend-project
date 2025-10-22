package com.backend.profileservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillDTO {
    @NotBlank
    private String name;
    @NotNull
    private Integer level;
    private String category;         // optional khi trả về
}
