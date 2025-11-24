package com.backend.profileservice.dto.request.student.education;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationUpdateRequest {

    private String school;
    private String major;
    private String degree;
    private Float gpa;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
