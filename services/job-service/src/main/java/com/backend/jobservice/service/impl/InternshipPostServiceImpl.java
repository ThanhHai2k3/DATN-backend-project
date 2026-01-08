package com.backend.jobservice.service.impl;

import com.backend.jobservice.client.AiNlpClient;
import com.backend.jobservice.client.ProfileClient;
import com.backend.jobservice.client.SkillClient;
import com.backend.jobservice.dto.JobNormUpdatedEvent;
import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.request.JobSkillRequest;
import com.backend.jobservice.dto.request.ProcessPostRequest;
import com.backend.jobservice.dto.response.*;
import com.backend.jobservice.entity.InternshipPost;
import com.backend.jobservice.entity.InternshipPostNorm;
import com.backend.jobservice.entity.JobSkill;
import com.backend.jobservice.enums.ErrorCode;
import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.exception.AppException;
import com.backend.jobservice.mapper.InternshipPostMapper;
import com.backend.jobservice.mapper.JobSkillMapper;
import com.backend.jobservice.repository.InternshipPostRepository;
import com.backend.jobservice.repository.JobSkillRepository;
import com.backend.jobservice.repository.specification.InternshipPostSpecification;
import com.backend.jobservice.service.InternshipPostService;
import com.backend.jobservice.service.JobNormEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final AiNlpClient aiNlpClient;
    private final ObjectMapper objectMapper;
    private final JobNormEventPublisher jobNormEventPublisher;


    @Override
    public InternshipPostResponse getPostDetailForAdmin(UUID id) {
        InternshipPost post = internshipPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        InternshipPostResponse response = internshipPostMapper.toResponse(post);
        fillSkillNames(response.getSkills());

        return response;
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    public InternshipPostResponse createPost(UUID employerUserId, InternshipPostRequest request){
        InternshipPost post = internshipPostMapper.toEntity(request);
        post.setPostedBy(employerUserId);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        //Gọi profile-service lấy companyId của employer
        UUID companyId = resolveCompanyIdOrThrow(employerUserId);
        post.setCompanyId(companyId);

        InternshipPost saved = internshipPostRepository.save(post);

        //Validate and save job skills
        List<JobSkill> jobSkills = new ArrayList<>();

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

        //saved.getInternshipPostNorm().
        try {
            ProcessPostRequest nlpReq = buildProcessPostRequest(saved, request);
            ProcessPostResponse nlpRes = aiNlpClient.processJob(nlpReq);
            applyNlpResultToPost(saved, nlpRes);
            jobNormEventPublisher.publish(
                    JobNormUpdatedEvent.builder()
                            .internshipPostId(saved.getId())
                            .companyId(companyId)
                            .skillsNorm(
                                    saved.getInternshipPostNorm().getSkillsNorm() != null
                                            ? saved.getInternshipPostNorm().getSkillsNorm()
                                            : Collections.emptyList()
                            )
//                            .minYears(saved.getInternshipPostNorm().getMinYears())
                            .workMode(saved.getWorkMode() != null
                                    ? saved.getWorkMode().name()
                                    : null)
                            .locationLat(saved.getInternshipPostNorm().getLat())
                            .locationLon(saved.getInternshipPostNorm().getLon())
                            .updatedAt(OffsetDateTime.now())
                            .build()
            );

        } catch (Exception ex) {
            saved.setNlpStatus("ERROR");
            saved.setNlpError(ex.getMessage());
        }

        InternshipPostResponse response = internshipPostMapper.toResponse(saved);
        fillSkillNames(response.getSkills());
        return response;
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    public InternshipPostResponse updatePost(UUID employerUserId, UUID postId, InternshipPostUpdateRequest request){
        InternshipPost post = internshipPostRepository.findByIdAndPostedBy(postId, employerUserId)
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
    public InternshipPostResponse getEmployerPostDetail(UUID employerUserId, UUID postId) {

        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getPostedBy().equals(employerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền xem bài đăng này");
        }
        InternshipPostResponse response = internshipPostMapper.toResponse(post);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipPostResponse getPostDetail(UUID postId){
        InternshipPost post = internshipPostRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getExpiredAt() == null || !post.getExpiredAt().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.POST_EXPIRED);
        }

        InternshipPostResponse response = internshipPostMapper.toResponse(post);
        fillSkillNames(response.getSkills());

        return response;
    }



    @Override
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN')")
    public void hidePost(UUID employerUserId, UUID postId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

//        if (!post.getPostedBy().equals(employerUserId)) {
//            throw new AppException(ErrorCode.FORBIDDEN);
//        }
        if (post.getStatus() == PostStatus.ACTIVE) {
            post.setStatus(PostStatus.HIDDEN);
        }
        else if (post.getStatus() == PostStatus.HIDDEN) {
            post.setStatus(PostStatus.PENDING);
        }
        else if (post.getStatus() == PostStatus.PENDING) {
            post.setStatus(PostStatus.HIDDEN);
        }
        internshipPostRepository.save(post);
    }

//    @Override
//    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
//    public InternshipPostResponse approvePost(UUID postId, UUID adminId){
//        InternshipPost post = internshipPostRepository.findById(postId)
//                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
//
//        post.setStatus(PostStatus.ACTIVE);
//        post.setUpdatedAt(Instant.now());
//        InternshipPost saved = internshipPostRepository.save(post);
//
//        // TODO: gửi notification tới Employer
//        InternshipPostResponse response = internshipPostMapper.toResponse(saved);
//        fillSkillNames(response.getSkills());
//
//        return response;
//    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public InternshipPostResponse approvePost(UUID postId, UUID adminId){
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() != PostStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_POST_STATUS);
        }

        Instant now = Instant.now();

        post.setStatus(PostStatus.ACTIVE);
        post.setUpdatedAt(now);

        // set hạn 30 ngày kể từ lúc duyệt
        if (post.getExpiredAt() == null) {
            post.setExpiredAt(now.plus(Duration.ofDays(30)));
        }

        InternshipPost saved = internshipPostRepository.save(post);
        return internshipPostMapper.toResponse(saved);
    }


    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void rejectPost(UUID postId) {
        InternshipPost post = internshipPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() != PostStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_POST_STATUS);
        }

        post.setStatus(PostStatus.REJECTED);
        post.setUpdatedAt(Instant.now());
        internshipPostRepository.save(post);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public List<InternshipPostSummaryResponse> getPendingPosts() {
        List<InternshipPost> pendingPosts = internshipPostRepository
                .findByStatusOrderByCreatedAtDesc(PostStatus.PENDING);
        return internshipPostMapper.toSummaryResponseList(pendingPosts);
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public List<InternshipPostSummaryResponse> getRejectedAndHiddenPosts() {
        List<InternshipPost> hidden = internshipPostRepository.findByStatusOrderByCreatedAtDesc(PostStatus.HIDDEN);
        List<InternshipPost> rejected = internshipPostRepository.findByStatusOrderByCreatedAtDesc(PostStatus.REJECTED);

        List<InternshipPost> combined = Stream.concat(hidden.stream(), rejected.stream())
                .sorted(Comparator.comparing(InternshipPost::getCreatedAt).reversed())
                .collect(Collectors.toList());
        
        return internshipPostMapper.toSummaryResponseList(combined);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InternshipPostSummaryResponse> searchPosts(
            String keyword, String workMode,
            UUID skillId, UUID companyId,
            String location, Pageable pageable
    ) {

        Specification<InternshipPost> spec = Specification
                .where(InternshipPostSpecification.hasStatus(PostStatus.ACTIVE))
                .and(InternshipPostSpecification.notExpired());

        spec = spec.and(InternshipPostSpecification.hasKeyword(keyword))
                .and(InternshipPostSpecification.hasWorkMode(workMode))
                .and(InternshipPostSpecification.hasSkill(skillId))
                .and(InternshipPostSpecification.hasLocation(location));

        if (companyId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("companyId"), companyId));
        }

        Page<InternshipPost> postsPage = internshipPostRepository.findAll(spec, pageable);

        List<InternshipPost> posts = postsPage.getContent();
        List<InternshipPostSummaryResponse> summaries = posts.stream()
                .map(internshipPostMapper::toSummaryResponse)
                .collect(java.util.stream.Collectors.toList());

        fillCompanyNames(posts, summaries);

        return new org.springframework.data.domain.PageImpl<>(summaries, pageable, postsPage.getTotalElements());
    }

    private void fillCompanyNames(List<InternshipPost> posts, List<InternshipPostSummaryResponse> summaries) {
        if (posts == null || posts.isEmpty()) return;

        List<UUID> companyIds = posts.stream()
                .map(InternshipPost::getCompanyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (companyIds.isEmpty()) return;

        try {
            ApiResponse<List<CompanyBasicResponse>> api = profileClient.getCompaniesBatch(companyIds);
            if (api == null || api.getData() == null) return;

            Map<UUID, String> map = api.getData().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(CompanyBasicResponse::getId, CompanyBasicResponse::getName, (a, b) -> a));

            for (int i = 0; i < posts.size(); i++) {
                UUID cid = posts.get(i).getCompanyId();
                if (cid != null && map.containsKey(cid)) {
                    summaries.get(i).setCompanyName(map.get(cid));
                }
            }
        } catch (Exception ignored) {
            // profile-service down thì để companyName null, không làm fail search
        }
    }

    private void fillSkillNames(List<JobSkillResponse> skills) {
        if (skills == null || skills.isEmpty()) return;
        List<UUID> skillIds = skills.stream()
                .map(JobSkillResponse::getSkillId)
                .distinct()
                .collect(Collectors.toList());

        try {
            ApiResponse<List<SkillResponse>> response = skillClient.getSkillsBatch(skillIds);

            if (response.getData() != null) {
                Map<UUID, String> skillMap = response.getData().stream()
                        .collect(Collectors.toMap(SkillResponse::getId, SkillResponse::getName));

                for (JobSkillResponse js : skills) {
                    if (skillMap.containsKey(js.getSkillId())) {
                        js.setSkillName(skillMap.get(js.getSkillId()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching batch skills: " + e.getMessage());
        }
    }

    private ProcessPostRequest buildProcessPostRequest(InternshipPost post,
                                                       InternshipPostRequest originalReq) {

        return ProcessPostRequest.builder()
                .postId(post.getId().toString())
                .title(post.getTitle())
                .position(post.getPosition())
                .description(post.getDescription())
                .duration(post.getDuration())
                .location(post.getLocation())
                .workMode(
                        post.getWorkMode() != null
                                ? post.getWorkMode().name()
                                : null
                )
//                .salary(originalReq.getSalary())
                .skills(originalReq.getSkills())
                .build();
    }

    private void applyNlpResultToPost(InternshipPost post,
                                      ProcessPostResponse res) throws JsonProcessingException {
        if (res == null) return;

        InternshipPostNorm norm = new InternshipPostNorm();
        norm.setInternshipPostId(post.getId());
        norm.setInternshipPost(post);

        // skills_norm: lưu JSONB (String)
        if (res.getSkillsNorm() != null) {
//            String json = objectMapper.writeValueAsString(res.getSkillsNorm());
            norm.setSkillsNorm(res.getSkillsNorm());
        }

        norm.setExperienceYearsMin(res.getExperienceYearsMin());
        norm.setExperienceYearsMax(res.getExperienceYearsMax());
        norm.setExperienceLevel(res.getExperienceLevel());

        norm.setEducationLevels(null);
        norm.setMajors(null);

        if (res.getDomains() != null) {
            norm.setDomains(res.getDomains().toArray(new String[0]));
        }

        if (res.getWorkModesNorm() != null) {
            norm.setWorkModesNorm(res.getWorkModesNorm().toArray(new String[0]));
        }

        if (res.getLocationsNorm() != null) {
            norm.setLocationsNorm(res.getLocationsNorm().toArray(new String[0]));
        }

        norm.setDurationNormMonths(res.getDurationMonthsMin());

        norm.setLat(res.getLat());
        norm.setLon(res.getLon());

        norm.setModelVersion(res.getModelVersion());

        post.setInternshipPostNorm(norm);

        post.setNlpStatus("DONE");
        post.setNlpError(null);
        post.setProcessedAt(res.getProcessedAt());
    }

    private UUID resolveCompanyIdOrThrow(UUID employerUserId) {
        try {
            ApiResponse<UUID> api = profileClient.getMyCompanyId(
                    employerUserId.toString(),
                    "EMPLOYER"
            );

            if (api == null || api.getData() == null) {
                throw new AppException(ErrorCode.EMPLOYER_HAS_NO_COMPANY);
            }

            return api.getData();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<InternshipPostResponse> getMyPosts(UUID employerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<InternshipPost> postPage = internshipPostRepository.findByPostedBy(employerUserId, pageable);

        return postPage.map(internshipPostMapper::toResponse);
    }
}
