package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("""
        SELECT DISTINCT s FROM Student s
        LEFT JOIN FETCH s.educations
        LEFT JOIN FETCH s.experiences
        LEFT JOIN FETCH s.projects
        LEFT JOIN FETCH s.socialLinks
        LEFT JOIN FETCH s.skills sk
        WHERE s.userId = :userId
    """)
    Optional<Student> findByUserIdWithAllRelations(@Param("userId") UUID userId);

}
