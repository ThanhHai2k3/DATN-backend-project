package com.backend.jobservice.service.impl;

import com.backend.jobservice.client.ProfileClient;
import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.response.InternshipPostResponse;
import com.backend.jobservice.dto.response.InternshipPostSummaryResponse;
import com.backend.jobservice.entity.InternshipPost;
import com.backend.jobservice.entity.JobSkill;
import com.backend.jobservice.enums.ErrorCode;
import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.exception.AppException;
import com.backend.jobservice.mapper.InternshipPostMapper;
import com.backend.jobservice.mapper.JobSkillMapper;
import com.backend.jobservice.repository.InternshipPostRepository;
import com.backend.jobservice.repository.JobSkillRepository;
import com.backend.jobservice.service.InternshipPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipPostServiceImpl implements InternshipPostService {

    private final InternshipPostRepository internshipPostRepository;
    private final JobSkillRepository jobSkillRepository;
    private final InternshipPostMapper internshipPostMapper;
    private final JobSkillMapper jobSkillMapper;
    private final ProfileClient profileClient;

    @Override
    public InternshipPostResponse createPost(UUID employerId, InternshipPostRequest request){
        InternshipPost post = internshipPostMapper.toEntity(request);
        post.setPostedBy(employerId);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        //Gọi profile-service lấy companyId của employer
        UUID companyId = profileClient.getCompanyIdByEmployer(employerId);
        post.setCompanyId(companyId);

        InternshipPost saved = internshipPostRepository.save(post);

        if(request.getSkills() != null && request.getSkills().isEmpty()){
            List<JobSkill> jobSkills = request.getSkills().stream()
                    .map(jobSkillMapper::toEntity)
                    .peek(jobSkill -> jobSkill.setInternshipPost(saved))
                    .collect(Collectors.toList());

            jobSkillRepository.saveAll(jobSkills);
            saved.setJobSkills(jobSkills);
        }

        return internshipPostMapper.toResponse(saved);
    }

    @Override
    public InternshipPostResponse updatePost(UUID employerId, UUID postId, InternshipPostUpdateRequest request){
        InternshipPost post = internshipPostRepository.findByIdAndPostedBy(postId, employerId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND_OR_FORBIDDEN));

        if(post.getStatus().equals(PostStatus.EXPIRED)){
            throw new AppException(ErrorCode.POST_EXPIRED);
        }

        internshipPostMapper.updateEntityFromDto(request, post);
        post.setUpdatedAt(Instant.now());

        if (post.getStatus().equals(PostStatus.ACTIVE)) {
            post.setStatus(PostStatus.PENDING);
        }

        jobSkillRepository.deleteByInternshipPostId(post.getId());
        if (request.getSkills() != null) {
            List<JobSkill> newSkills = request.getSkills().stream()
                    .map(jobSkillMapper::toEntity)
                    .peek(js -> js.setInternshipPost(post))
                    .collect(Collectors.toList());
            jobSkillRepository.saveAll(newSkills);
            post.setJobSkills(newSkills);
        }

        return internshipPostMapper.toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipPostResponse getPostDetail(UUID postId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        return internshipPostMapper.toResponse(post);
    }

    @Override
    public void hidePost(UUID employerId, UUID postId){
        InternshipPost post = internshipPostRepository.findByIdAndPostedBy(postId, employerId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND_OR_FORBIDDEN));

        if(post.getStatus().equals(PostStatus.HIDDEN)){
            throw new AppException(ErrorCode.POST_ALREADY_HIDDEN);
        }

        post.setStatus(PostStatus.HIDDEN);
        post.setUpdatedAt(Instant.now());
        internshipPostRepository.save(post);
    }

    @Override
    public InternshipPostResponse approvePost(UUID postId, UUID adminId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setStatus(PostStatus.ACTIVE);
        post.setUpdatedAt(Instant.now());
        InternshipPost saved = internshipPostRepository.save(post);

        // TODO: gửi notification tới Employer
        return internshipPostMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipPostSummaryResponse> searchPosts(String keyword, String workMode, UUID skillId, UUID companyId){
        List<InternshipPost> found = internshipPostRepository
                .findByTitleContainingIgnoreCaseAndStatus(keyword, PostStatus.ACTIVE);

        // TODO: mở rộng filter workMode, skillId, companyId
        return internshipPostMapper.toSummaryResponseList(found);
    }
}
