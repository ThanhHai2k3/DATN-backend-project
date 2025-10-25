package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.CompanyRequest;
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
    public CompanyResponse create(UUID userId, CompanyRequest request){
        if(companyRepository.existsByNameIgnoreCase(request.getName())){
            throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
        }

        Company company = companyMapper.toEntity(request);
        company = companyRepository.save(company);

        //Tìm employer theo userId (người tạo công ty)
        Employer employer = employerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Employer newEmployer = new Employer();
                    newEmployer.setUserId(userId);
                    newEmployer.setName("HR Admin");
                    newEmployer.setPosition("Admin");
                    return newEmployer;
                });
        //Gắn employer này vào công ty và set quyền admin
        employer.setCompany(company);
        employer.setAdmin(true);
        employerRepository.save(employer);

        return companyMapper.toResponse(company);
    }

    public CompanyResponse updateByUser(UUID userId, CompanyRequest request) {
        // Lấy employer theo userId
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        // Chỉ admin của công ty được phép sửa
        if (!employer.isAdmin()) {
            throw new AppException(ErrorCode.UPDATE_COMPANY_DENIED);
        }

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        companyMapper.update(company, request);
        companyRepository.save(company);

        return companyMapper.toResponse(company);
    }

    // Lấy công ty theo userId (company mà user thuộc về)
    @Override
    public CompanyResponse getByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        return companyMapper.toResponse(company);
    }

    @Override
    public List<CompanyResponse> getAll(){
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    //Xoá công ty mà user đang quản lý (chỉ admin công ty)
    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));

        if (!employer.isAdmin()) {
            throw new AppException(ErrorCode.UPDATE_COMPANY_DENIED);
        }

        Company company = employer.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        companyRepository.delete(company);
    }
}
