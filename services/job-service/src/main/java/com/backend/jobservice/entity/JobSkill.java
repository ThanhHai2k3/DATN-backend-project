package com.backend.jobservice.entity;

import com.backend.jobservice.enums.ImportanceLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "job_skills", schema = "job_schema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_post_id", nullable = false)
    private InternshipPost internshipPost;

    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance_level")
    private ImportanceLevel importanceLevel;

    private String note;
}
