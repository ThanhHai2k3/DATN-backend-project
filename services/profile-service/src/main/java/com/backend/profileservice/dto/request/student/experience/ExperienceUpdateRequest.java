package com.backend.profileservice.dto.request.student.experience;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceUpdateRequest {

    private String companyName;
    private String position;
    private String description;
    private String achievement;
    private LocalDate startDate;
    private LocalDate endDate;
}
