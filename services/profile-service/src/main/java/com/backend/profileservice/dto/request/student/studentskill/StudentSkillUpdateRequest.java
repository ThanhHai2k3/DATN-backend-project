package com.backend.profileservice.dto.request.student.studentskill;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillUpdateRequest {

    private String level;
    private String years;
    private String note;
}
