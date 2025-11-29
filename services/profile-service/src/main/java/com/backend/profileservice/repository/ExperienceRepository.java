package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Education;
import com.backend.profileservice.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    List<Experience> findByStudentId(UUID studentId);

    Optional<Experience> findByIdAndStudentUserId(UUID experienceId, UUID userId);

    void deleteByStudentId(UUID studentId);
}