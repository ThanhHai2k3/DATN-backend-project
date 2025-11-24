package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private UUID id;
    private String projectName;
    private String projectUrl;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
