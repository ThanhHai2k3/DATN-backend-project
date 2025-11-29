package com.backend.profileservice.dto.request.student.studentskill;

import com.backend.profileservice.enums.SkillLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillUpdateRequest {

    private SkillLevel level;
    private String years;
    private String note;
}
