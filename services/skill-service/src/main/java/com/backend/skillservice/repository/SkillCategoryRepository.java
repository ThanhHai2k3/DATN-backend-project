package com.backend.skillservice.repository;

import com.backend.skillservice.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, UUID> {
    Optional<SkillCategory> findByNameIgnoreCase(String name);
}
