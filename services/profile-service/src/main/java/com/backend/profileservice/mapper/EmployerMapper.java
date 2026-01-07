package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.EmployerUpdateRequest;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Employer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface EmployerMapper {

    EmployerResponse toResponse(Employer employer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateProfile(@MappingTarget Employer entity, EmployerUpdateRequest request);
}
