package com.backend.jobservice.dto.response;

import com.backend.jobservice.enums.ImportanceLevel;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkillResponse {
    private UUID id;
    private UUID skillId;                // FK sang bảng skills
    private String skillName;            // hiển thị ra cho FE
    private ImportanceLevel importanceLevel;
    private String note;
}
