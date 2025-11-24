package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.student.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.student.SocialLinkResponse;

import java.util.List;
import java.util.UUID;

public interface SocialLinkService {

    SocialLinkResponse create(UUID userId, SocialLinkCreateRequest request);

    SocialLinkResponse update(UUID userId, UUID linkId, SocialLinkUpdateRequest request);

    void delete(UUID userId, UUID linkId);

    List<SocialLinkResponse> getAllByStudent(UUID userId);
}
