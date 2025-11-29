package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.student.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.student.SocialLinkResponse;
import com.backend.profileservice.entity.SocialLink;
import com.backend.profileservice.enums.SocialType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SocialLinkMapper {

    @Mapping(target = "type",
            expression = "java(request.getType() != null ? com.backend.profileservice.enums.SocialType.valueOf(request.getType().toUpperCase()) : null)")
    SocialLink toEntity(SocialLinkCreateRequest request);

    @Mapping(target = "type",
            expression = "java(entity.getType() != null ? entity.getType().name() : null)")
    SocialLinkResponse toResponse(SocialLink entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget SocialLink entity, SocialLinkUpdateRequest request);
}