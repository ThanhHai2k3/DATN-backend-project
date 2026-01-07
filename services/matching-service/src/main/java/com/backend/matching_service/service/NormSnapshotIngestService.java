package com.backend.matching_service.service;

import com.backend.matching_service.dto.ingest.CvNormUpdatedEvent;
import com.backend.matching_service.dto.ingest.JobNormUpdatedEvent;
import com.backend.matching_service.repository.CvNormSnapshotRepository;
import com.backend.matching_service.repository.JobNormSnapshotRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NormSnapshotIngestService {

    private final CvNormSnapshotRepository cvRepo;
    private final JobNormSnapshotRepository jobRepo;
    private final ObjectMapper objectMapper;

    public void upsertCv(CvNormUpdatedEvent event) {
        if (event == null || event.getCvId() == null) {
            throw new IllegalArgumentException("CvNormUpdatedEvent is null or missing cvId");
        }
        if (event.getStudentId() == null) {
            throw new IllegalArgumentException("CvNormUpdatedEvent missing studentId for cvId=" + event.getCvId());
        }

        OffsetDateTime updatedAt = event.getUpdatedAt() != null ? event.getUpdatedAt() : OffsetDateTime.now();

        cvRepo.upsert(
                event.getCvId(),
                event.getStudentId(),
                toJsonOrThrow(event.getSkillsNorm()),
                toJsonOrThrow(event.getExperienceAreas()),
                toJsonOrThrow(event.getExperienceTitles()),
                event.getEducationLevel(),
                toJsonOrThrow(event.getEducationMajors()),
                event.getYearsTotal(),
                event.getModelVersion(),
                updatedAt
        );
    }

    public void upsertJob(JobNormUpdatedEvent event) {
        if (event == null || event.getInternshipPostId() == null) {
            throw new IllegalArgumentException("JobNormUpdatedEvent is null or missing internshipPostId");
        }

        OffsetDateTime updatedAt = event.getUpdatedAt() != null ? event.getUpdatedAt() : OffsetDateTime.now();

        jobRepo.upsert(
                event.getInternshipPostId(),
                event.getCompanyId(),
                toJsonOrThrow(event.getSkillsNorm()),
                toJsonOrThrow(event.getExperienceAreas()),
                toJsonOrThrow(event.getExperienceTitles()),
                event.getEducationLevel(),
                toJsonOrThrow(event.getEducationMajors()),
                event.getMinYears(),
                event.getWorkMode(),
                event.getLocationLat(),
                event.getLocationLon(),
                updatedAt
        );
    }

    private String toJsonOrThrow(List<String> data) {
        try {
            List<String> safe = (data == null) ? Collections.emptyList() : data;
            return objectMapper.writeValueAsString(safe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize list to JSON", e);
        }
    }

    @SuppressWarnings("unused")
    private String toJsonOrEmpty(List<String> data) {
        try {
            List<String> safe = (data == null) ? Collections.emptyList() : data;
            return objectMapper.writeValueAsString(safe);
        } catch (Exception e) {
            return "[]";
        }
    }
}
