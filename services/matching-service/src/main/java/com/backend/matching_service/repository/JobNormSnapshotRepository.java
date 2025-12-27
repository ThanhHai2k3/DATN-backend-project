package com.backend.matching_service.repository;

import com.backend.matching_service.entity.JobNormSnapshotEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface JobNormSnapshotRepository
        extends JpaRepository<JobNormSnapshotEntity, UUID> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO matching_schema.job_norm_snapshot (
            internship_post_id, company_id,
            skills_norm, experience_areas, experience_titles,
            education_level, education_majors,
            min_years, work_mode, location_lat, location_lon,
            updated_at
        )
        VALUES (
            :postId, :companyId,
            CAST(:skillsNorm AS jsonb),
            CAST(:experienceAreas AS jsonb),
            CAST(:experienceTitles AS jsonb),
            :educationLevel,
            CAST(:educationMajors AS jsonb),
            :minYears,
            :workMode,
            :lat,
            :lon,
            :updatedAt
        )
        ON CONFLICT (internship_post_id)
        DO UPDATE SET
            company_id        = EXCLUDED.company_id,
            skills_norm       = EXCLUDED.skills_norm,
            experience_areas  = EXCLUDED.experience_areas,
            experience_titles = EXCLUDED.experience_titles,
            education_level   = EXCLUDED.education_level,
            education_majors  = EXCLUDED.education_majors,
            min_years         = EXCLUDED.min_years,
            work_mode         = EXCLUDED.work_mode,
            location_lat      = EXCLUDED.location_lat,
            location_lon      = EXCLUDED.location_lon,
            updated_at        = EXCLUDED.updated_at
        """, nativeQuery = true)
    void upsert(
            @Param("postId") UUID postId,
            @Param("companyId") UUID companyId,
            @Param("skillsNorm") String skillsNorm,
            @Param("experienceAreas") String experienceAreas,
            @Param("experienceTitles") String experienceTitles,
            @Param("educationLevel") String educationLevel,
            @Param("educationMajors") String educationMajors,
            @Param("minYears") BigDecimal minYears,
            @Param("workMode") String workMode,
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}
