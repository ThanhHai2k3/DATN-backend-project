package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, UUID> {
    List<Employer> findByCompanyId(UUID companyId);
    Optional<Employer> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
