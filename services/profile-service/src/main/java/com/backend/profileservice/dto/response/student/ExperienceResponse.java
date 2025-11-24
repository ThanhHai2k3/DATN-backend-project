package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceResponse {

    private UUID id;
    private String companyName;
    private String position;
    private String description;
    private String achievement;
    private LocalDate startDate;
    private LocalDate endDate;
}
