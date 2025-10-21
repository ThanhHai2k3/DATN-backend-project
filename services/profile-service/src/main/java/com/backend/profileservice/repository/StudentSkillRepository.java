package com.backend.profileservice.repository;

import com.backend.profileservice.entity.StudentSkill;
import com.backend.profileservice.entity.StudentSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, StudentSkillId> {
    List<StudentSkill> findByStudentId(UUID studentId);
    void deleteByStudentId(UUID studentId);
}
