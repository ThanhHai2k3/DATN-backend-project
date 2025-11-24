package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByStudentId(UUID studentId);
    void deleteByStudentId(UUID studentId);
}
