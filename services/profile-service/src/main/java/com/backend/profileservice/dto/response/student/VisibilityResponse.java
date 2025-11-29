package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisibilityResponse {

    private UUID studentId;
    private boolean publicProfile;
}
