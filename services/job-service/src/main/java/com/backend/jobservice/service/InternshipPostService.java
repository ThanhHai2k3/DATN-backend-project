package com.backend.jobservice.service;

import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.response.InternshipPostResponse;
import com.backend.jobservice.dto.response.InternshipPostSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface InternshipPostService {
    InternshipPostResponse createPost(UUID employerId, InternshipPostRequest request);
    InternshipPostResponse updatePost(UUID employerId, UUID postId, InternshipPostUpdateRequest request);
    void hidePost(UUID employerId, UUID postId);

    InternshipPostResponse getPostDetail(UUID postId);
    List<InternshipPostSummaryResponse> searchPosts(String keyword, String workMode, UUID skillId, UUID companyId, String location);

    InternshipPostResponse approvePost(UUID postId, UUID adminId);

    void rejectPost(UUID postId);

    List<InternshipPostSummaryResponse> getPendingPosts();
}