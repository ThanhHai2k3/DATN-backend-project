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
public class InternshipPostSummaryResponse {
    private UUID id;
    private String title;
    private String position;
    private String location;
    private WorkMode workMode;
    private PostStatus status;
    private Instant expiredAt;
    private Instant createdAt;
    private String companyName; // có thể lấy từ profile-service qua REST call
}
