package com.backend.matching_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cv_norm_snapshot", schema = "matching_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CvNormSnapshotEntity {

    @Id
    @Column(name = "cv_id")
    private Long cvId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
