package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Employer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface EmployerMapper {
    EmployerResponse toResponse(Employer employer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Employer entity, EmployerRequest request);
}
