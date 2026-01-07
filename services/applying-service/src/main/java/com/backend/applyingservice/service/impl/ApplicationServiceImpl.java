package com.backend.applyingservice.service.impl;

import com.backend.applyingservice.client.CvClient;
import com.backend.applyingservice.client.JobClient;
import com.backend.applyingservice.client.ProfileClient;
import com.backend.applyingservice.dto.external.InternshipPostResponse;
import com.backend.applyingservice.dto.external.StudentResponse;
import com.backend.applyingservice.dto.request.ApplyRequest;
import com.backend.applyingservice.dto.response.ApplicationResponse;
import com.backend.applyingservice.entity.Application;
import com.backend.applyingservice.enums.ApplicationStatus;
import com.backend.applyingservice.enums.ErrorCode;
import com.backend.applyingservice.exception.AppException;
import com.backend.applyingservice.repository.ApplicationRepository;
import com.backend.applyingservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CvClient cvClient;
    private final JobClient jobClient;
    private final ProfileClient profileClient;

    @Override
    @Transactional
    public ApplicationResponse applyJob(UUID studentId, ApplyRequest request) {
        // 1. Check duplicate apply
        if (applicationRepository.existsByStudentIdAndJobPostId(studentId, request.getJobPostId())) {
            throw new AppException(ErrorCode.DUPLICATE_APPLICATION);
        }

        // 2. Validate Job Post (Gọi Job Service)
        var jobApiResponse = jobClient.getPostDetail(request.getJobPostId());
        if (jobApiResponse == null || !jobApiResponse.isSuccess() || jobApiResponse.getData() == null) {
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND, "Job post not found");
        }

        InternshipPostResponse jobData = jobApiResponse.getData();
        
        if (!"ACTIVE".equalsIgnoreCase(jobData.getStatus())) {
            throw new AppException(ErrorCode.JOB_POST_NOT_ACTIVE);
        }

        // 3. Validate CV (Gọi CV Service)
        try {
            var cvData = cvClient.getCvById(request.getCvId(), studentId.toString(), "STUDENT");
            if (cvData == null) throw new AppException(ErrorCode.CV_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error verifying CV ownership: {}", e.getMessage());
            throw new AppException(ErrorCode.CV_NOT_FOUND, "CV not found or access denied");
        }

        // 4. Save Application
        Application app = Application.builder()
                .jobPostId(request.getJobPostId())
                .employerId(jobData.getPostedBy())
                .studentId(studentId)
                .cvId(request.getCvId())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Application savedApp = applicationRepository.save(app);

        // 5. Build Response
        return mapToResponse(savedApp, jobData.getTitle(), null);
    }

    @Override
    @Transactional
    public ApplicationResponse getApplicationDetailForEmployer(UUID employerId, UUID applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        // Validate Ownership (Chỉ Employer sở hữu bài đăng mới được xem)
        if (!app.getEmployerId().equals(employerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // AUTO-VIEWED LOGIC: Chuyển status sang VIEWED nếu đang là SUBMITTED
        if (app.getStatus() == ApplicationStatus.SUBMITTED) {
            app.setStatus(ApplicationStatus.VIEWED);
            app.setViewedAt(Instant.now());
            app.setUpdatedAt(Instant.now());
            applicationRepository.save(app);
        }

        // Fetch data enrichment (Student Profile & Job Title)
        String studentName = "Unknown";
        String studentAvatar = null;
        String jobTitle = "Unknown Job";

        // Lấy thông tin Student
        try {
            var profileRes = profileClient.getStudentFullName(app.getStudentId());

            if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                studentName = profileRes.getData();
            }
        } catch (Exception e) {
            log.warn("Could not fetch student name for app {}: {}", applicationId, e.getMessage());
        }

        // Lấy thông tin Job (để hiển thị title)
        try {
            var jobRes = jobClient.getPostDetail(app.getJobPostId());
            if(jobRes != null && jobRes.isSuccess() && jobRes.getData() != null) {
                jobTitle = jobRes.getData().getTitle();
            }
        } catch (Exception ignored){}

        String cvUrl = null;
        try {
            var cvRes = cvClient.getCvById(
                    app.getCvId(),
                    app.getStudentId().toString(),
                    "STUDENT"
            );
            if (cvRes != null) {
                cvUrl = cvRes.getCvUrl();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch CV URL for app {}: {}", applicationId, e.getMessage());
        }

        // Map response
        ApplicationResponse response = mapToResponse(app, jobTitle, null);
        response.setStudentName(studentName);
        response.setStudentAvatar(studentAvatar);
        response.setCvUrl(cvUrl);

        return response;
    }

    @Override
    public List<ApplicationResponse> getMyApplications(UUID studentId) {
        // Lấy list từ DB
        List<Application> apps = applicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId);

        // Map sang response (Gọi Job Service để lấy Title)
        // Chưa có cache tránh vòng lặp N + 1
        return apps.stream().map(app -> {
            String jobTitle = "Unknown Job";
            String companyName = "Unknown Company";

            try {
                var jobRes = jobClient.getPostDetail(app.getJobPostId());
                if (jobRes != null && jobRes.isSuccess() && jobRes.getData() != null) {
                    jobTitle = jobRes.getData().getTitle();
                    companyName = "Company ID: " + jobRes.getData().getCompanyId();
                }
            } catch (Exception ignored) {}

            return mapToResponse(app, jobTitle, companyName);
        }).collect(Collectors.toList());
    }

    // --- Helper Mapper ---
    private ApplicationResponse mapToResponse(Application app, String jobTitle, String companyName) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobPostId(app.getJobPostId())
                .jobTitle(jobTitle)
                .companyName(companyName)
                .studentId(app.getStudentId())
                .cvId(app.getCvId())
                .coverLetter(app.getCoverLetter())
                .status(app.getStatus())
                .note(app.getNote())
                .appliedAt(app.getAppliedAt())
                .viewedAt(app.getViewedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByPostId(UUID employerId, UUID jobPostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applicationPage = applicationRepository.findByJobPostIdAndEmployerId(jobPostId, employerId, pageable);

        if (applicationPage.isEmpty()) {
            return Page.empty();
        }

        String jobTitle = "Unknown Job";
        try {
            var jobRes = jobClient.getPostDetail(jobPostId);
            if (jobRes != null && jobRes.getData() != null) jobTitle = jobRes.getData().getTitle();
        } catch (Exception ignored) {}
        final String finalJobTitle = jobTitle;

        List<UUID> studentIds = applicationPage.getContent().stream()
                .map(Application::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, StudentResponse> tempMap = new HashMap<>();

        if (!studentIds.isEmpty()) {
            try {
                var profileRes = profileClient.getStudentsBatch(studentIds);
                if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                    tempMap = profileRes.getData().stream()
                            .collect(Collectors.toMap(
                                    StudentResponse::getUserId,
                                    Function.identity(),
                                    (existing, replacement) -> existing
                            ));
                }
            } catch (Exception e) {
                log.error("Failed to batch fetch profiles", e);
            }
        }

        final Map<UUID, StudentResponse> studentMap = tempMap;

        return applicationPage.map(app -> {
            StudentResponse studentInfo = studentMap.get(app.getStudentId());

            String studentName = (studentInfo != null) ? studentInfo.getFullName() : "Unknown Candidate";
            String studentAvatar = (studentInfo != null) ? studentInfo.getAvatarUrl() : null;

            ApplicationResponse res = mapToResponse(app, finalJobTitle, null);
            res.setStudentName(studentName);
            res.setStudentAvatar(studentAvatar);

            return res;
        });
    }

    @Override
    @Transactional
    public void updateApplicationStatus(UUID employerId, UUID applicationId, ApplicationStatus status, String note) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!app.getEmployerId().equals(employerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        app.setStatus(status);

        app.setNote(note);

        app.setUpdatedAt(Instant.now());

        applicationRepository.save(app);
    }
}