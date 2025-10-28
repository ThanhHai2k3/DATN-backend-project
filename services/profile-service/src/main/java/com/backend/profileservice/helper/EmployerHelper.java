package com.backend.profileservice.helper;

import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployerHelper {
    private final EmployerRepository employerRepository;

    @Transactional
    public Employer ensureEmployerAdmin(UUID userId, Company company, String name, String position) {
        return employerRepository.findByUserId(userId)
                .map(existing -> {
                    // Cập nhật thông tin nếu cần
                    existing.setName(name);
                    existing.setPosition(position);
                    existing.setCompany(company);
                    existing.setIsAdmin(true);
                    return employerRepository.saveAndFlush(existing);
                })
                .orElseGet(() -> {
                    // Chỉ tạo mới khi chưa có
                    Employer e = Employer.builder()
                            .userId(userId)
                            .name(name)
                            .position(position)
                            .company(company)
                            .isAdmin(true)
                            .build();
                    return employerRepository.saveAndFlush(e);
                });
    }
}
