package com.backend.matching_service.service;

import com.backend.matching_service.dto.ingest.CvNormUpdatedEvent;
import com.backend.matching_service.dto.ingest.JobNormUpdatedEvent;
import com.backend.matching_service.repository.CvNormSnapshotRepository;
import com.backend.matching_service.repository.JobNormSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NormSnapshotIngestService {

    private final CvNormSnapshotRepository cvRepo;
    private final JobNormSnapshotRepository jobRepo;
    private final ObjectMapper objectMapper;

    /**
     * Ingest CV_NORM event -> upsert cv_norm_snapshot
     */
    public void upsertCv(CvNormUpdatedEvent event) {
        cvRepo.upsert(
                event.getCvId(),
                event.getStudentId(),
                toJson(event.getSkillsNorm()),
                toJson(event.getExperienceAreas()),
                toJson(event.getExperienceTitles()),
                event.getEducationLevel(),
                toJson(event.getEducationMajors()),
                event.getYearsTotal(),
                event.getModelVersion(),
                event.getUpdatedAt() != null
                        ? event.getUpdatedAt()
                        : OffsetDateTime.now()
        );
    }

    /**
     * Ingest JOB_NORM event -> upsert job_norm_snapshot
     */
    public void upsertJob(JobNormUpdatedEvent event) {
        jobRepo.upsert(
                event.getInternshipPostId(),
                event.getCompanyId(),
                toJson(event.getSkillsNorm()),
                toJson(event.getExperienceAreas()),
                toJson(event.getExperienceTitles()),
                event.getEducationLevel(),
                toJson(event.getEducationMajors()),
                event.getMinYears(),
                event.getWorkMode(),
                event.getLocationLat(),
                event.getLocationLon(),
                event.getUpdatedAt() != null
                        ? event.getUpdatedAt()
                        : OffsetDateTime.now()
        );
    }

    /**
     * Safe convert List -> JSON string
     */
    private String toJson(List<String> data) {
        try {
            return objectMapper.writeValueAsString(
                    data == null ? List.of() : data
            );
        } catch (Exception e) {
            return "[]";
        }
    }
}
