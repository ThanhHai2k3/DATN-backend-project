package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.CompanyUpdateRequest;
import com.backend.profileservice.dto.response.CompanyBasicResponse;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.CompanyMapper;
import com.backend.profileservice.repository.CompanyRepository;
import com.backend.profileservice.repository.EmployerRepository;
import com.backend.profileservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse create(UUID userId, CompanyCreateRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
        }

        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        if (employer.getCompany() != null) {
            throw new AppException(ErrorCode.EMPLOYER_ALREADY_HAS_COMPANY);
        }

        Company company = companyMapper.toEntity(request);
        company = companyRepository.saveAndFlush(company);

        employer.setCompany(company);
        employer.setAdmin(true);
        employerRepository.saveAndFlush(employer);

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse updateByUser(UUID userId, CompanyUpdateRequest request) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        // nếu đổi tên thì check trùng (tránh đụng company khác)
        if (request.getName() != null && !request.getName().isBlank()) {
            if (companyRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), company.getId())) {
                throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
            }
        }

        companyMapper.update(company, request);
        companyRepository.save(company);

        return companyMapper.toResponse(company);
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse getByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserIdWithCompany(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public void deleteByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        // khuyến nghị giữ admin cho delete để tránh xoá nhầm
        if (!employer.isAdmin()) {
            throw new AppException(ErrorCode.UPDATE_COMPANY_DENIED);
        }

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        List<Employer> companyEmployers = employerRepository.findAllByCompanyId(company.getId());
        for (Employer e : companyEmployers) {
            e.setCompany(null);
            e.setAdmin(false);
        }
        employerRepository.saveAll(companyEmployers);

        companyRepository.delete(company);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public CompanyResponse getById(UUID companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        return companyMapper.toResponse(company);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<CompanyResponse> getAll(){
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("permitAll()")
    public List<CompanyBasicResponse> getBasicBatch(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        return companyRepository.findAllById(ids).stream()
                .map(c -> CompanyBasicResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .toList();
    }
}
