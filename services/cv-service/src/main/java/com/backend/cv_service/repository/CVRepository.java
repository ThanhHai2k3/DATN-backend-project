package com.backend.cv_service.repository;

import com.backend.cv_service.entity.CV;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    List<CV> findByStudentId(UUID studentId);

    Optional<CV> findByIdAndStudentId(Long id, UUID studentId);

    boolean existsByStudentId(UUID studentId);

    boolean existsByIdAndStudentId(Long id, UUID studentId);

    Optional<CV> findDetailedByIdAndStudentId(Long id, UUID studentId);

    Optional<CV> findDetailedById(Long id);
}