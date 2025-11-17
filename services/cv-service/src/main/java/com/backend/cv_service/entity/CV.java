package com.backend.cv_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cvs", schema = "cv_schema")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "cv_name", nullable = false)
    private String cvName;

    @Column(name = "cv_url", nullable = false)
    private String cvUrl;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Lob
    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "nlp_status")
    private String nlpStatus; // PENDING / SUCCESS / FAILED

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // Quan hệ 1–1 với CvNorm (optional nhưng hợp lý)
    @OneToOne(mappedBy = "cv", fetch = FetchType.LAZY)
    private CvNorm cvNorm;
}
