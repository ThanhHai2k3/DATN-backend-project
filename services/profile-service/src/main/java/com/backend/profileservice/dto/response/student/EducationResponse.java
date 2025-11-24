package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {

    private UUID id;
    private String school;
    private String major;
    private String degree;
    private Float gpa;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
