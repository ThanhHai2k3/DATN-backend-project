package com.backend.profileservice.dto.request.student.project;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdateRequest {

    private String projectName;
    private String projectUrl;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
