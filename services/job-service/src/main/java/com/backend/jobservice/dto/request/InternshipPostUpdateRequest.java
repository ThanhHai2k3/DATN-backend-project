package com.backend.jobservice.dto.request;

import com.backend.jobservice.enums.WorkMode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipPostUpdateRequest {
    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private WorkMode workMode;

    private List<JobSkillRequest> skills;
}
