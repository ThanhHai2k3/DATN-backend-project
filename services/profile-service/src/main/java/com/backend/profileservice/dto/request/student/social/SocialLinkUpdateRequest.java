package com.backend.profileservice.dto.request.student.social;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkUpdateRequest {

    private String url;
}
