package com.backend.profileservice.dto.request.student;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUpdateRequest {

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
