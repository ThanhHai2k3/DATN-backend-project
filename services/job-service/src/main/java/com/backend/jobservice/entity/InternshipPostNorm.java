package com.backend.jobservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="internship_post_norm", schema = "job_schema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternshipPostNorm {
    @Id
    @Column(name = "internship_post_id")
    private UUID internshipPostId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "internship_post_id")
    private InternshipPost internshipPost;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills_norm", columnDefinition = "jsonb")
    private List<String> skillsNorm = new ArrayList<>();

    private BigDecimal experienceYearsMin;
    private BigDecimal experienceYearsMax;
    private String experienceLevel;

    @Column(name = "education_levels")
    private String[] educationLevels;

    @Column(name = "majors")
    private String[] majors;

    @Column(name = "domains")
    private String[] domains;

    @Column(name = "work_modes_norm")
    private String[] workModesNorm;

    @Column(name = "locations_norm")
    private String[] locationsNorm;

    private Double lat;
    private Double lon;


    private BigDecimal durationNormMonths;

    private String modelVersion;

    private OffsetDateTime processedAt;
}
