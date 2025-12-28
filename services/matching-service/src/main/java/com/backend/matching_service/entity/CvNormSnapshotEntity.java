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
@Table(name = "cv_norm_snapshot", schema = "matching_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvNormSnapshotEntity {

    @Id
    @Column(name = "cv_id")
    private Long cvId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

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

    @Column(name = "years_total", precision = 5, scale = 2)
    private BigDecimal yearsTotal;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
