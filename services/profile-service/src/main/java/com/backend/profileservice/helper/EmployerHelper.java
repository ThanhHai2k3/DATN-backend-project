package com.backend.profileservice.helper;

import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployerHelper {
    private final EmployerRepository employerRepository;

    public Employer ensureEmployerAdmin(UUID userId, Company company, String name, String position) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Employer e = new Employer();
                    e.setUserId(userId);
                    e.setName(name != null ? name : "HR Admin");
                    e.setPosition(position != null ? position : "Admin");
                    return e;
                });

        employer.setCompany(company);
        employer.setAdmin(true);
        return employerRepository.save(employer);
    }
}
