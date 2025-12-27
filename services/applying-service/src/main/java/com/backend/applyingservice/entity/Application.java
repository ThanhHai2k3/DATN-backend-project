package com.backend.applyingservice.entity;

import com.backend.applyingservice.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applications", schema = "applying_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_post_id", nullable = false)
    private UUID jobPostId;

    @Column(name = "employer_id", nullable = false)
    private UUID employerId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String coverLetter;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    @Column(name = "viewed_at")
    private Instant viewedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
