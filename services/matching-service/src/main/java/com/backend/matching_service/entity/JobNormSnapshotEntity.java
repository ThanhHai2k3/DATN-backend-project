package com.backend.matching_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_norm_snapshot", schema = "matching_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobNormSnapshotEntity {

    @Id
    @Column(name = "internship_post_id")
    private UUID internshipPostId;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
