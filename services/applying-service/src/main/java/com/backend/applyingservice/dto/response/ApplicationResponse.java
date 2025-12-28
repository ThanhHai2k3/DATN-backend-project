package com.backend.applyingservice.dto.response;

import com.backend.applyingservice.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {
    private UUID id;

    private UUID jobPostId;
    private String jobTitle;
    private String companyName;

    private UUID studentId;
    private String studentName;
    private String studentAvatar;
    private String studentHeadline;

    private Long cvId;
    private String cvUrl;
    private String coverLetter;

    private ApplicationStatus status;
    private Instant appliedAt;
    private Instant viewedAt;
    private Instant updatedAt;
}