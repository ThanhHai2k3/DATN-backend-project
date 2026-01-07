package com.backend.profileservice.repository;

import com.backend.profileservice.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, UUID> {

    Optional<Employer> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    List<Employer> findAllByCompanyId(UUID companyId);

    @Query("""
        select e
        from Employer e
        left join fetch e.company
        where e.userId = :userId
    """)
    Optional<Employer> findByUserIdWithCompany(@Param("userId") UUID userId);

    @Query("""
        select e
        from Employer e
        left join fetch e.company
        where e.company.id = :companyId
    """)
    List<Employer> findAllByCompanyIdWithCompany(@Param("companyId") UUID companyId);
}
