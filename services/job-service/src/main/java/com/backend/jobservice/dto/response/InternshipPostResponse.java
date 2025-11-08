package com.backend.jobservice.dto.response;

import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.enums.WorkMode;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipPostResponse {
    private UUID id;
    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private WorkMode workMode;
    private PostStatus status;

    private UUID companyId;
    private UUID postedBy;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiredAt;

    private List<JobSkillResponse> skills;
}
