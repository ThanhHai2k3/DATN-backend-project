package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Employer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface EmployerMapper {

    //@Mapping(target = "isAdmin", source = "admin")
    EmployerResponse toResponse(Employer employer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Employer entity, EmployerRequest request);
}
