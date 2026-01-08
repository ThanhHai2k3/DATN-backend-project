package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.EmployerUpdateRequest;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.EmployerMapper;
import com.backend.profileservice.repository.CompanyRepository;
import com.backend.profileservice.repository.EmployerRepository;
import com.backend.profileservice.service.CompanyService;
import com.backend.profileservice.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final EmployerMapper employerMapper;
    private final CompanyService companyService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse getByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserIdWithCompany(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse updateMyProfile(UUID userId, EmployerUpdateRequest request) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        employerMapper.updateProfile(employer, request);

        Employer saved = employerRepository.saveAndFlush(employer);
        return employerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse joinCompany(UUID userId, UUID companyId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        if (employer.getCompany() != null) {
            throw new AppException(ErrorCode.EMPLOYER_ALREADY_HAS_COMPANY);
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        employer.setCompany(company);
        employer.setAdmin(false);

        Employer saved = employerRepository.saveAndFlush(employer);
        return employerMapper.toResponse(saved);
    }

    /**
     * Khi không tìm thấy company -> tạo mới và tự join.
     * Nơi set admin=true hợp lý nhất là lúc tạo company.
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse createCompanyAndJoin(UUID userId, CompanyCreateRequest request) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        if (employer.getCompany() != null) {
            throw new AppException(ErrorCode.EMPLOYER_ALREADY_HAS_COMPANY);
        }

        // CompanyService.create(...) đã:
        // - tạo company
        // - gán employer.company = company
        // - set employer.admin = true (owner)
        companyService.create(userId, request);

        // reload employer để trả về response mới nhất
        Employer updatedEmployer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        return employerMapper.toResponse(updatedEmployer);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER')")
    public void leaveCompany(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        //admin không được rời company
        if (employer.isAdmin() && employer.getCompany() != null) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN_EMPLOYER);
        }

        employer.setCompany(null);
        employer.setAdmin(false);
        employerRepository.save(employer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('EMPLOYER','SYSTEM_ADMIN')")
    public List<EmployerResponse> getAllByCompany(UUID callerUserId, UUID companyId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        // Nếu caller là admin -> cho xem tất cả
        if (!isSystemAdmin()) {
            // caller là EMPLOYER -> chỉ được xem company của chính mình
            Employer caller = employerRepository.findByUserIdWithCompany(callerUserId)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

            if (caller.getCompany() == null || !caller.getCompany().getId().equals(companyId)) {
                // bạn có thể dùng ErrorCode riêng: ACCESS_DENIED / FORBIDDEN
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }

        return employerRepository.findAllByCompanyIdWithCompany(companyId)
                .stream()
                .map(employerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public EmployerResponse getPublicProfile(UUID viewerUserId, UUID targetUserId) {
        Employer target = employerRepository.findByUserIdWithCompany(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        return employerMapper.toResponse(target);
    }

    @Override
    @PreAuthorize("permitAll()")
    public String getFullNameByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        return employer.getName();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public UUID getMyCompanyId(UUID userId) {

        Employer employer = employerRepository.findByUserIdWithCompany(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        if (employer.getCompany() == null) {
            throw new AppException(ErrorCode.EMPLOYER_HAS_NO_COMPANY);
        }

        return employer.getCompany().getId();
    }

    @Override
    @Transactional
    @PreAuthorize("permitAll()")
    public void autoCreateProfile(UUID userId, String fullName) {
        if (employerRepository.existsByUserId(userId)) return;

        Employer employer = Employer.builder()
                .userId(userId)
                .name(fullName)
                .admin(false)
                .build();

        employerRepository.save(employer);
    }

    private boolean isSystemAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;

        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_SYSTEM_ADMIN".equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
