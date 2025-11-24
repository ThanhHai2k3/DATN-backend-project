package com.backend.profileservice.dto.response.student;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkResponse {

    private UUID id;
    private String type;
    private String url;
}
