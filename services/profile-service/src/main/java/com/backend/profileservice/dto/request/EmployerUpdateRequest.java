package com.backend.profileservice.dto.request;

import com.backend.profileservice.enums.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerUpdateRequest {
    private String name;
    private Gender gender;
    private String position;
}
