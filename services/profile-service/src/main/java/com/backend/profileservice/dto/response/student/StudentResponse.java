package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private UUID id;
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
    private Instant createdAt;
    private Instant updatedAt;

    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
    private List<StudentSkillResponse> skills;
    private List<ProjectResponse> projects;
    private List<SocialLinkResponse> socialLinks;
}
