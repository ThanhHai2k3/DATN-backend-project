package com.backend.matching_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_norm_snapshot", schema = "matching_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobNormSnapshotEntity {

    @Id
    @Column(name = "internship_post_id")
    private UUID internshipPostId;

    @Column(name = "company_id")
    private UUID companyId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills_norm", columnDefinition = "jsonb", nullable = false)
    private JsonNode skillsNorm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience_areas", columnDefinition = "jsonb", nullable = false)
    private JsonNode experienceAreas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience_titles", columnDefinition = "jsonb", nullable = false)
    private JsonNode experienceTitles;

    @Column(name = "education_level", length = 50)
    private String educationLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education_majors", columnDefinition = "jsonb", nullable = false)
    private JsonNode educationMajors;

    @Column(name = "min_years", precision = 5, scale = 2)
    private BigDecimal minYears;

    @Column(name = "work_mode", length = 20)
    private String workMode;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lon")
    private Double locationLon;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
