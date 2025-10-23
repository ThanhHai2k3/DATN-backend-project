package com.backend.profileservice.dto.response;

import com.backend.profileservice.dto.EducationDTO;
import com.backend.profileservice.dto.ExperienceDTO;
import com.backend.profileservice.dto.StudentSkillDTO;
import com.backend.profileservice.enums.Gender;
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
public class StudentProfileResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String avatarUrl;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private String bio;
    private String cvUrl;
    private String cvText;
    private boolean visible;
    private Instant createdAt;
    private Instant updatedAt;
    private List<EducationDTO> educations;
    private List<ExperienceDTO> experiences;
    private List<StudentSkillDTO> skills; // name + level
}
