package com.backend.jobservice.entity;

import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "internship_posts", schema = "job_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String position;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode;

    private String duration;
    private String location;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "company_id")
    private UUID companyId; //FK to profile_schema.companies

    @Column(name = "posted_by")
    private UUID postedBy;

    @OneToMany(mappedBy = "internshipPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobSkill> jobSkills = new ArrayList<>();
}
