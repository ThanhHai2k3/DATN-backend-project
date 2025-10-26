package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.CompanyRequest;
import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.helper.EmployerHelper;
import com.backend.profileservice.mapper.EmployerMapper;
import com.backend.profileservice.repository.CompanyRepository;
import com.backend.profileservice.repository.EmployerRepository;
import com.backend.profileservice.service.CompanyService;
import com.backend.profileservice.service.EmployerService;
import lombok.RequiredArgsConstructor;
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
    private final EmployerHelper employerHelper;

    @Override
    public EmployerResponse getByUserId(UUID userId){
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional
    public EmployerResponse createOrJoinCompany(UUID userId, EmployerRequest request) {
        // Mỗi user chỉ có thể có một employer profile duy nhất
        if (employerRepository.existsByUserId(userId)) {
            throw new AppException(ErrorCode.EMPLOYER_PROFILE_EXISTED);
        }

        // Khởi tạo entity Employer
        Employer employer = new Employer();
        employer.setUserId(userId);
        employer.setName(request.getName());
        employer.setPosition(request.getPosition());

        Company company;

        // Trường hợp 1: join vào công ty có sẵn
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
            employer.setCompany(company);
            employer.setAdmin(false);
            employerRepository.save(employer);
        }
        // Trường hợp 2: chưa có công ty → auto-create tạm (fallback cho giai đoạn phát triển BE)
        // Sau này khi FE có form tạo công ty riêng, sẽ bỏ đoạn này đi
        else {
            // Tạo company mới qua CompanyService
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setName("Company" + request.getName());
            companyRequest.setDescription("New company created by " + request.getName());
            companyRequest.setIndustry("Unknown");

            CompanyResponse companyResponse = companyService.create(userId, companyRequest);
            company = companyRepository.findById(companyResponse.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

            // Gọi helper: gắn user này làm admin cho company vừa tạo
            employerHelper.ensureEmployerAdmin(userId, company, request.getName(), request.getPosition());
        }

        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional
    public EmployerResponse updateProfile(UUID userId, EmployerRequest request) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        employerMapper.update(employer, request);
        employerRepository.save(employer);

        return employerMapper.toResponse(employer);
    }

    @Override
    public List<EmployerResponse> getAllByCompany(UUID companyId){
        return employerRepository.findAllByCompanyId(companyId)
                .stream()
                .map(employerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        //Nếu employer là admin công ty → không cho xóa trực tiếp khi vẫn còn company
        if (employer.isAdmin() && employer.getCompany() != null) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN_EMPLOYER);
        }

        //HR bình thường -> unlink khỏi công ty
        employer.setCompany(null);
        employer.setAdmin(false);

        employerRepository.save(employer);
    }
}
