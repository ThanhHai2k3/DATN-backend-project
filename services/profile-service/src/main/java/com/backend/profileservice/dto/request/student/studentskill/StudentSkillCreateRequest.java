package com.backend.profileservice.dto.request.student.studentskill;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillCreateRequest {

    private String skillId; //Nếu FE chọn từ danh sách → gửi skillId
    private String skillName; //Nếu FE nhập tên mới → gửi skillName
    private String categoryId; //Thêm khi skillName != null để thêm vào category
    private String level;
    private Integer years;
    private String note;
}