package com.backend.profileservice.dto.request.student.social;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkCreateRequest {

    private String type;
    private String url;
}
