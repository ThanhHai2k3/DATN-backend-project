package com.backend.authservice.mapper;

import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.time.Instant.class})
public interface AuthMapper {
    // Map RegisterRequest -> UserAccount
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "passwordHash", ignore = true) // set trong service
    UserAccount toUserAccountEntity(RegisterRequest request);

    // Map UserAccount -> AuthResponse
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresInSeconds", ignore = true)
    @Mapping(target = "role", source = "role")
    AuthResponse toAuthResponse(UserAccount userAccount);
}
