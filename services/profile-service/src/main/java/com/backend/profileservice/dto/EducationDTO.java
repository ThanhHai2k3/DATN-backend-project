package com.backend.profileservice.dto;

import com.backend.profileservice.enums.Degree;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDTO {
    private UUID id;
    @NotBlank
    private String schoolName;
    private String major;
    private Degree degree;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
