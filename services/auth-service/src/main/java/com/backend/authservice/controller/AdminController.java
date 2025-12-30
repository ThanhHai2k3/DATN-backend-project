package com.backend.authservice.controller;

import com.backend.authservice.dto.admin.UpdateUserRoleRequest;
import com.backend.authservice.dto.admin.UpdateUserStatusRequest;
import com.backend.authservice.dto.response.ApiResponse;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.Role;
import com.backend.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ========== A) USERS ==========
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> listUsers(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("ADMIN_200", "List users success",
                adminService.listUsers(q, role, status, page, size)));
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserDetail(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success("ADMIN_201", "Get user detail success",
                adminService.getUserDetail(userId)));
    }

    // ========== B) STATUS ==========
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<?>> updateUserStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserStatusRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success("ADMIN_203", "Update user status success",
                adminService.updateUserStatus(userId, req)));
    }

    // ========== C) ROLE ==========
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<?>> updateUserRole(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserRoleRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success("ADMIN_204", "Update user role success",
                adminService.updateUserRole(userId, req)));
    }

}
