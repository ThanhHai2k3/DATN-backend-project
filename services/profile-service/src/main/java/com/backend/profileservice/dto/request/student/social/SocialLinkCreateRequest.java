package com.backend.profileservice.dto.request.student.social;

import com.backend.profileservice.enums.SocialType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkCreateRequest {

    private SocialType type;
    private String url;
}
