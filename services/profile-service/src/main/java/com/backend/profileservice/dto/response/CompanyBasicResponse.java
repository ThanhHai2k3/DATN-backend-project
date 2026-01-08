package com.backend.profileservice.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyBasicResponse {

    private UUID id;
    private String name;
}