package com.backend.authservice.service;

import com.backend.authservice.dto.admin.UpdateUserRoleRequest;
import com.backend.authservice.dto.admin.UpdateUserStatusRequest;
import com.backend.authservice.dto.admin.AdminUserDetail;
import com.backend.authservice.dto.admin.AdminUserSummary;
import com.backend.authservice.dto.admin.PageResponse;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.Role;

import java.util.UUID;

public interface AdminService {
    PageResponse<AdminUserSummary> listUsers(String q, String role, String status, int page, int size);
    AdminUserDetail getUserDetail(UUID userId);
    AdminUserDetail updateUserStatus(UUID userId, UpdateUserStatusRequest req);
    AdminUserDetail updateUserRole(UUID userId, UpdateUserRoleRequest req);
}
