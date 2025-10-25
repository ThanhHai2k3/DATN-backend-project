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
    public CompanyResponse create(UUID creatorUserId, CompanyRequest request){
        if(companyRepository.existsByNameIgnoreCase(request.getName())){
            throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
        }

        Company company = companyMapper.toEntity(request);
        company = companyRepository.save(company);

        //Tìm employer theo userId (người tạo công ty)
        Employer employer = employerRepository.findByUserId(creatorUserId)
                .orElseGet(() -> {
                    // Nếu user vừa đăng ký chưa có hồ sơ Employer, tạo mới luôn
                    Employer newEmployer = new Employer();
                    newEmployer.setUserId(creatorUserId);
                    newEmployer.setName("HR Admin"); // có thể cập nhật lại từ frontend sau
                    newEmployer.setPosition("Admin");
                    return newEmployer;
                });
        //Gắn employer này vào công ty và set quyền admin
        employer.setCompany(company);
        employer.setAdmin(true);
        employerRepository.save(employer);

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse update(UUID companyId, CompanyRequest request, UUID actorUserId){
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        boolean isAdmin = employerRepository.findByUserId(actorUserId)
                .filter(employer -> employer.getCompany().getId().equals(companyId) && employer.isAdmin())
                .isPresent();
        if(!isAdmin){
            throw new AppException(ErrorCode.UPDATE_COMPANY_DENIED);
        }

        companyMapper.update(company, request);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    public CompanyResponse getById(UUID companyId){
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        return companyMapper.toResponse(company);
    }

    @Override
    public List<CompanyResponse> getAll(){
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID companyId, UUID actorUserId){
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        boolean isAdmin = employerRepository.findByUserId(actorUserId)
                .filter(emp -> emp.getCompany().getId().equals(companyId) && emp.isAdmin())
                .isPresent();
        if (!isAdmin) {
            throw new AppException(ErrorCode.UPDATE_COMPANY_DENIED);
        }

        companyRepository.delete(company);
    }
}
