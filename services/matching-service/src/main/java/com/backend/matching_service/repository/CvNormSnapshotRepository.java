package com.backend.matching_service.repository;

import com.backend.matching_service.entity.CvNormSnapshotEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface CvNormSnapshotRepository
        extends JpaRepository<CvNormSnapshotEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO matching_schema.cv_norm_snapshot (
            cv_id, student_id,
            skills_norm, experience_areas, experience_titles,
            education_level, education_majors,
            years_total, model_version, updated_at
        )
        VALUES (
            :cvId, :studentId,
            CAST(:skillsNorm AS jsonb),
            CAST(:experienceAreas AS jsonb),
            CAST(:experienceTitles AS jsonb),
            :educationLevel,
            CAST(:educationMajors AS jsonb),
            :yearsTotal,
            :modelVersion,
            :updatedAt
        )
        ON CONFLICT (cv_id)
        DO UPDATE SET
            student_id        = EXCLUDED.student_id,
            skills_norm       = EXCLUDED.skills_norm,
            experience_areas  = EXCLUDED.experience_areas,
            experience_titles = EXCLUDED.experience_titles,
            education_level   = EXCLUDED.education_level,
            education_majors  = EXCLUDED.education_majors,
            years_total       = EXCLUDED.years_total,
            model_version     = EXCLUDED.model_version,
            updated_at        = EXCLUDED.updated_at
        """, nativeQuery = true)
    void upsert(
            @Param("cvId") Long cvId,
            @Param("studentId") UUID studentId,
            @Param("skillsNorm") String skillsNorm,
            @Param("experienceAreas") String experienceAreas,
            @Param("experienceTitles") String experienceTitles,
            @Param("educationLevel") String educationLevel,
            @Param("educationMajors") String educationMajors,
            @Param("yearsTotal") BigDecimal yearsTotal,
            @Param("modelVersion") String modelVersion,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}
