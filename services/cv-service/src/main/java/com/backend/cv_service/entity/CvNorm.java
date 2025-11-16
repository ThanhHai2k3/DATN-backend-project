package com.backend.cv_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "cvId")
@Entity
@Table(name = "cv_norm", schema = "cv_schema")
@Check(constraints = "education_level IS NULL OR education_level IN ('none','associate','bachelor','master','phd','other')")
public class CvNorm {


    @Id
    @Column(name = "cv_id")
    private Long cvId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                   // dùng chung PK với entity CV
    @JoinColumn(name = "cv_id")
    private CV cv;

    @Column(name = "years_total", columnDefinition = "numeric(5,2)")
    private Double yearsTotal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience_titles", columnDefinition = "jsonb", nullable = false)
    private List<String> experienceTitles = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience_areas", columnDefinition = "jsonb", nullable = false)
    private List<String> experienceAreas = new ArrayList<>();

    @Column(name = "education_level")
    private String educationLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education_majors", columnDefinition = "jsonb", nullable = false)
    private List<String> educationMajors = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills_norm", columnDefinition = "jsonb", nullable = false)
    private List<String> skillsNorm = new ArrayList<>();

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "processed_at", columnDefinition = "timestamptz")
    private OffsetDateTime processedAt;
}
