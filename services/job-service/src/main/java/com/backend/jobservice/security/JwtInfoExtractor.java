package com.backend.jobservice.security;

import com.backend.jobservice.enums.ErrorCode;
import com.backend.jobservice.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtInfoExtractor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    public UUID extractUserId(HttpServletRequest request){
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if(userIdHeader == null || userIdHeader.isBlank()){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        try{
            return UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException ex){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public String extractUserRole(HttpServletRequest request){
        String role = request.getHeader(USER_ROLE_HEADER);

        if(role == null || role.isBlank()){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return role;
    }

    public boolean isEmployer(HttpServletRequest request) {
        return "EMPLOYER".equalsIgnoreCase(extractUserRole(request));
    }

    public boolean isAdmin(HttpServletRequest request) {
        return "ADMIN".equalsIgnoreCase(extractUserRole(request));
    }

    public boolean isStudent(HttpServletRequest request) {
        return "STUDENT".equalsIgnoreCase(extractUserRole(request));
    }
}
