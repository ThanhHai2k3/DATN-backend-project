package com.backend.cv_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "educations", schema = "cv_schema")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "university_name", nullable = false)
    private String universityName;

    @Column(nullable = false)
    private String major;

    @Column(name = "original_gpa", precision = 4, scale = 2)
    private BigDecimal originalGpa;

    @Column(name = "gpa_scale")
    private Integer gpaScale;

    @Column(name = "normalized_gpa", precision = 4, scale = 2)
    private BigDecimal normalizedGpa;

    // Mối quan hệ Nhiều-Một với CV
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;
}