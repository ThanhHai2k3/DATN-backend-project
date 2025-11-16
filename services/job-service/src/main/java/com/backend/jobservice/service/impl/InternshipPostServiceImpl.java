package com.backend.jobservice.service.impl;

import com.backend.jobservice.client.ProfileClient;
import com.backend.jobservice.client.SkillClient;
import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.request.JobSkillRequest;
import com.backend.jobservice.dto.response.*;
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
import java.util.ArrayList;
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
    private final SkillClient skillClient;

    @Override
    public InternshipPostResponse createPost(UUID employerId, InternshipPostRequest request){
        InternshipPost post = internshipPostMapper.toEntity(request);
        post.setPostedBy(employerId);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        //Gọi profile-service lấy companyId của employer
//        UUID companyId = profileClient.getCompanyIdByEmployer(employerId);
//        post.setCompanyId(companyId);
        post.setCompanyId(UUID.randomUUID()); // TODO: profile-client

        InternshipPost saved = internshipPostRepository.save(post);

        //Validate and save job skills
        List<JobSkill> jobSkills = new ArrayList<>();

//        if(request.getSkills() != null && !request.getSkills().isEmpty()){
//            jobSkills = request.getSkills().stream()
//                    .map(jobSkillMapper::toEntity)
//                    .peek(jobSkill -> jobSkill.setInternshipPost(saved))
//                    .collect(Collectors.toList());
//
//            jobSkillRepository.saveAll(jobSkills);
//        }
        if(request.getSkills() != null){
            for(JobSkillRequest skillRequest : request.getSkills()){
                // FE bắt buộc gửi skillId trong job-service
                if(skillRequest.getSkillId() == null){
                    throw new AppException(ErrorCode.SKILL_ID_REQUIRED);
                }

                UUID skillId;
                try{
                    skillId = UUID.fromString(skillRequest.getSkillId());
                } catch (Exception e){
                    throw new AppException(ErrorCode.INVALID_UUID);
                }

                ApiResponse<SkillResponse> api = skillClient.getSkillById(skillId);
                SkillResponse skillResponse = api.getData();
                if (skillResponse == null) {
                    throw new AppException(ErrorCode.SKILL_NOT_FOUND);
                }

                // Map JobSkill entity
                JobSkill jobSkill = new JobSkill();
                jobSkill.setSkillId(skillId);
                jobSkill.setInternshipPost(saved);
                jobSkill.setImportanceLevel(skillRequest.getImportanceLevel());
                jobSkill.setNote(skillRequest.getNote());

                jobSkills.add(jobSkill);
            }
        }

        jobSkillRepository.saveAll(jobSkills);
        saved.setJobSkills(jobSkills);

        InternshipPostResponse response = internshipPostMapper.toResponse(saved);
        fillSkillNames(response.getSkills());
        return response;
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

        List<JobSkill> newSkills = new ArrayList<>();

        if (request.getSkills() != null) {
            // Xóa toàn bộ skill cũ trong entity
            post.getJobSkills().clear();

            // Xóa trong DB
            jobSkillRepository.deleteByInternshipPostId(post.getId());

            for(JobSkillRequest skillRequest : request.getSkills()){
                if(skillRequest.getSkillId() == null){
                    throw new AppException(ErrorCode.SKILL_ID_REQUIRED);
                }

                UUID skillId;
                try{
                    skillId = UUID.fromString(skillRequest.getSkillId());
                } catch (Exception e){
                    throw new AppException(ErrorCode.INVALID_UUID);
                }

                ApiResponse<SkillResponse> api = skillClient.getSkillById(skillId);
                SkillResponse skillResponse = api.getData();
                if (skillResponse == null) {
                    throw new AppException(ErrorCode.SKILL_NOT_FOUND);
                }

                JobSkill jobSkill = new JobSkill();
                jobSkill.setSkillId(skillId);
                jobSkill.setInternshipPost(post);
                jobSkill.setImportanceLevel(skillRequest.getImportanceLevel());
                jobSkill.setNote(skillRequest.getNote());

                newSkills.add(jobSkill);
            }
            post.getJobSkills().addAll(newSkills);
            jobSkillRepository.saveAll(newSkills);
        }

        InternshipPostResponse response = internshipPostMapper.toResponse(post);
        fillSkillNames(response.getSkills());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipPostResponse getPostDetail(UUID postId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        InternshipPostResponse response = internshipPostMapper.toResponse(post);
        fillSkillNames(response.getSkills());

        return response;
    }

    @Override
    public void hidePost(UUID employerId, UUID postId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getPostedBy().equals(employerId)) {
            throw new AppException(ErrorCode.HIDE_POST_DENIED);
        }

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
        InternshipPostResponse response = internshipPostMapper.toResponse(saved);
        fillSkillNames(response.getSkills());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipPostSummaryResponse> searchPosts(String keyword, String workMode, UUID skillId, UUID companyId){
        List<InternshipPost> found = internshipPostRepository
                .findByTitleContainingIgnoreCaseAndStatus(keyword, PostStatus.ACTIVE);

        // TODO: mở rộng filter workMode, skillId, companyId
        return internshipPostMapper.toSummaryResponseList(found);
    }

    private void fillSkillNames(List<JobSkillResponse> skills) {
        if (skills == null) return;

        for (JobSkillResponse js : skills) {
            ApiResponse<SkillResponse> api = skillClient.getSkillById(js.getSkillId());
            SkillResponse skill = api.getData();
            if (skill != null) {
                js.setSkillName(skill.getName());
            }
        }
    }
}
