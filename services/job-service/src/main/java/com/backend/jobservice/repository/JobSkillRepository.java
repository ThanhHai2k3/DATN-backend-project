package com.backend.jobservice.repository;

import com.backend.jobservice.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkill, UUID> {
    List<JobSkill> findByInternshipPostId(UUID internshipPostId);
    void deleteByInternshipPostId(UUID internshipPostId);
}
