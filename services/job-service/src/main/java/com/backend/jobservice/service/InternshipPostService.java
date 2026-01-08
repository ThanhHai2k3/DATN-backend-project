package com.backend.jobservice.service;

import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.response.InternshipPostResponse;
import com.backend.jobservice.dto.response.InternshipPostSummaryResponse;

import java.util.List;
import java.util.UUID;

import com.backend.jobservice.entity.InternshipPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface InternshipPostService {

    InternshipPostResponse createPost(UUID employerUserId, InternshipPostRequest request);

    InternshipPostResponse updatePost(UUID employerUserId, UUID postId, InternshipPostUpdateRequest request);

    void hidePost(UUID employerUserId, UUID postId);

    InternshipPostResponse getPostDetail(UUID postId);

    Page<InternshipPostSummaryResponse> searchPosts(String keyword, String workMode, UUID skillId, UUID companyId, String location, Pageable pageable);

    InternshipPostResponse approvePost(UUID postId, UUID adminId);

    void rejectPost(UUID postId);

    List<InternshipPostSummaryResponse> getPendingPosts();

    Page<InternshipPostResponse> getMyPosts(UUID employerUserId, int page, int size);

    InternshipPostResponse getEmployerPostDetail(UUID employerUserId, UUID postId);

    InternshipPostResponse getPostDetailForAdmin(UUID id);

    List<InternshipPostSummaryResponse> getRejectedAndHiddenPosts();
}
