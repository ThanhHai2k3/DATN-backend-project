package com.backend.matching_service.service;

import com.backend.matching_service.dto.JobMatchResult;
import com.backend.matching_service.dto.JobMatchingRequest;
import com.backend.matching_service.entity.CvNormSnapshotEntity;
import com.backend.matching_service.entity.JobNormSnapshotEntity;
import com.backend.matching_service.repository.CvNormSnapshotRepository;
import com.backend.matching_service.repository.JobNormSnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final CvNormSnapshotRepository cvRepo;
    private final JobNormSnapshotRepository jobRepo;

    @Override
    public List<JobMatchResult> matchJobs(UUID userId, JobMatchingRequest request) {

        if (request == null || request.getCvId() == null) {
            throw new IllegalArgumentException("cvId is required");
        }

        CvNormSnapshotEntity cv = cvRepo.findById(request.getCvId())
                .orElseThrow(() -> new IllegalArgumentException("CV not found: " + request.getCvId()));

        if (!cv.getStudentId().equals(userId)) {
            throw new SecurityException("CV does not belong to user");
        }

        Double cvLat = request.getDesiredLat();
        Double cvLon = request.getDesiredLon();

        Set<String> cvSkills = jsonArrayToSet(cv.getSkillsNorm());

        List<JobNormSnapshotEntity> jobs = jobRepo.findAll();

        return jobs.stream()
                .map(job -> calculateMatch(cvSkills, cvLat, cvLon, request, job))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(JobMatchResult::getScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Calculate matching score for 1 job
     */
    private JobMatchResult calculateMatch(
            Set<String> cvSkills,
            Double cvLat,
            Double cvLon,
            JobMatchingRequest request,
            JobNormSnapshotEntity job
    ) {

        Set<String> jobSkills = jsonArrayToSet(job.getSkillsNorm());

        if (jobSkills.isEmpty()) {
            return null;
        }

        Set<String> matched = new HashSet<>(cvSkills);
        matched.retainAll(jobSkills);

        double skillScore = (double) matched.size() / jobSkills.size();

        Double distanceKm = null;
        double locationScore = 1.0;

        if (cvLat != null && cvLon != null
                && job.getLocationLat() != null && job.getLocationLon() != null) {

            distanceKm = haversine(
                    cvLat, cvLon,
                    job.getLocationLat(), job.getLocationLon()
            );

            if (request.getMaxDistanceKm() != null
                    && distanceKm > request.getMaxDistanceKm()) {
                return null;
            }

            locationScore =
                    distanceKm <= 5  ? 1.0 :
                            distanceKm <= 15 ? 0.7 :
                                    distanceKm <= 30 ? 0.4 : 0.1;
        }

        double finalScore = 0.7 * skillScore + 0.3 * locationScore;

        if (finalScore < 0.2) {
            return null;
        }

        return JobMatchResult.builder()
                .internshipPostId(job.getInternshipPostId())
                .companyId(job.getCompanyId())
                .score(round(finalScore))
                .skillScore(round(skillScore))
                .matchedSkills(new ArrayList<>(matched))
                .distanceKm(distanceKm != null ? round(distanceKm) : null)
                .locationScore(round(locationScore))
                .build();
    }

    /**
     * Convert JSON array -> Set<String>
     */
    private Set<String> jsonArrayToSet(JsonNode node) {
        if (node == null || !node.isArray()) return Collections.emptySet();
        Set<String> set = new HashSet<>();
        node.forEach(n -> set.add(n.asText()));
        return set;
    }

    /**
     * Haversine distance (km)
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
