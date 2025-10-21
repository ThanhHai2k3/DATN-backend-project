package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findByStudentId(UUID studentId);
    void deleteByStudentId(UUID studentId);
}
