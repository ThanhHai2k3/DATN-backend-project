package com.backend.profileservice.dto.request.student;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreateRequest {

    private UUID userId;
    private String fullName;
    private String avatarUrl;
    private String headline;
    private LocalDate dob;
    private String gender;
    private String phone;
    private String address;
    private String bio;
    private boolean publicProfile;
}
