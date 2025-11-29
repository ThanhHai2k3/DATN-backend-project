package com.backend.profileservice.repository;

import com.backend.profileservice.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, UUID> {

    List<StudentSkill> findByStudentId(UUID studentId);

    Optional<StudentSkill> findByIdAndStudentUserId(UUID skillId, UUID userId);

    void deleteByStudentId(UUID studentId);

    boolean existsByStudentIdAndSkillId(UUID studentId, UUID skillId);
}
