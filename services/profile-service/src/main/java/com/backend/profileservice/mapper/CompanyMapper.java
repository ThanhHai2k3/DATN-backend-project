package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.CompanyUpdateRequest;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.entity.Company;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyCreateRequest request);

    CompanyResponse toResponse(Company company);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Company target, CompanyUpdateRequest request);
}
