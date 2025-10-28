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

//        // Khởi tạo entity Employer (profile)
//        Employer employer = new Employer();
//        employer.setUserId(userId);
//        employer.setName(request.getName());
//        employer.setPosition(request.getPosition());

//        // Validate name bắt buộc
//        if (request.getName() == null || request.getName().trim().isEmpty()) {
//            throw new AppException(ErrorCode.INVALID_REQUEST, "Name is required");
//        }

        if (request.getCompanyId() != null) {
            // Trường hợp 1: join vào công ty có sẵn
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

//            employer.setCompany(company);
//            employer.setAdmin(false);
            Employer employer = Employer.builder()
                    .userId(userId)
                    .name(request.getName())
                    .position(request.getPosition())
                    .company(company)
                    .isAdmin(false)
                    .build();

            Employer saved = employerRepository.saveAndFlush(employer);
            return employerMapper.toResponse(saved);
        } else {
            // Trường hợp 2: chưa có công ty → auto-create tạm (fallback cho giai đoạn phát triển BE)
            // Sau này khi FE có form tạo công ty riêng, sẽ bỏ đoạn này đi
            // Tạo company mới qua CompanyService
            CompanyRequest companyRequest = CompanyRequest.builder()
                    .name("Company " + request.getName())
                    .description("New company created by " + request.getName())
                    .industry("Unknown")
                    .build();

            CompanyResponse companyResponse = companyService.create(userId, companyRequest);
            Company company = companyRepository.findById(companyResponse.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

            // Dùng helper – sẽ tạo employer với đúng name/position
//            Employer adminEmployer = employerHelper.ensureEmployerAdmin(
//                    userId,
//                    company,
//                    request.getName(),
//                    request.getPosition()
//            );

            Employer adminEmployer = Employer.builder()
                    .userId(userId)
                    .name(request.getName())
                    .position(request.getPosition())
                    .company(company)
                    .isAdmin(true)
                    .build();

            Employer saved = employerRepository.saveAndFlush(adminEmployer);
            return employerMapper.toResponse(saved);
        }
    }

    @Override
    @Transactional
    public EmployerResponse updateProfile(UUID userId, EmployerRequest request) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        employerMapper.update(employer, request);
        Employer saved = employerRepository.saveAndFlush(employer);
        return employerMapper.toResponse(saved);
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

        // Nếu là admin công ty, không thể xóa khi công ty vẫn tồn tại
        // Admin phải xóa công ty trước hoặc chuyển quyền admin
        if (employer.isIsAdmin() && employer.getCompany() != null) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN_EMPLOYER);
        }

        //HR bình thường -> unlink khỏi công ty
        employer.setCompany(null);
        employer.setIsAdmin(false);

        employerRepository.save(employer);
    }
}
