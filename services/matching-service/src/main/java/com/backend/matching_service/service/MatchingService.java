package com.backend.matching_service.service;

import com.backend.matching_service.dto.JobMatchResult;
import com.backend.matching_service.dto.JobMatchingRequest;

import java.util.List;
import java.util.UUID;

public interface MatchingService {

    /**
     * Find matching jobs for a user's CV
     *
     * @param userId  user id from X-User-Id header
     * @param request matching request (cvId, desired location, filters)
     * @return ranked list of matching jobs
     */
    List<JobMatchResult> matchJobs(UUID userId, JobMatchingRequest request);
}
