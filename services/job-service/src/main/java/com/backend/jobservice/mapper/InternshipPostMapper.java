package com.backend.jobservice.mapper;

import com.backend.jobservice.dto.request.InternshipPostRequest;
import com.backend.jobservice.dto.request.InternshipPostUpdateRequest;
import com.backend.jobservice.dto.response.InternshipPostResponse;
import com.backend.jobservice.dto.response.InternshipPostSummaryResponse;
import com.backend.jobservice.entity.InternshipPost;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JobSkillMapper.class})
public interface InternshipPostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)       // set PENDING trong service
    @Mapping(target = "companyId", ignore = true)    // lấy từ profile-Service
    @Mapping(target = "postedBy", ignore = true)     // lấy từ JWT
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    InternshipPost toEntity(InternshipPostRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    InternshipPost toEntity(InternshipPostUpdateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(InternshipPostUpdateRequest dto, @MappingTarget InternshipPost entity);

    InternshipPostResponse toResponse(InternshipPost entity);

    List<InternshipPostResponse> toResponseList(List<InternshipPost> entities);

    //danh sách/search
    @Mapping(target = "companyName", ignore = true)
    InternshipPostSummaryResponse toSummaryResponse(InternshipPost entity);

    List<InternshipPostSummaryResponse> toSummaryResponseList(List<InternshipPost> entities);
}
