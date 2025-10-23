package com.backend.profileservice.dto.request;

import com.backend.profileservice.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileRequest {
    @NotNull
    private UUID userId; // tạm thời; khi có JWT thì lấy từ token

    //@NotBlank
    private String fullName;

    private String avatarUrl;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private String bio;
    private String cvUrl;
    private String cvText;
    private Boolean visible; // optional; default true nếu null
}
