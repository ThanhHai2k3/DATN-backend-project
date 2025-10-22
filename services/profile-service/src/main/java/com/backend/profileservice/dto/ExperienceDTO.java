package com.backend.profileservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceDTO {
    private UUID id;
    private String projectName;
    private String companyName;
    private String role;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
