package com.backend.jobservice.dto.request;

import com.backend.jobservice.dto.request.JobSkillRequest;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPostRequest {

    private String postId;          // UUID dạng String

    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private String workMode;        // ONSITE / REMOTE / HYBRID
    private String salary;

    // Skills do employer nhập thủ công khi đăng bài
    private List<JobSkillRequest> skills;

}
