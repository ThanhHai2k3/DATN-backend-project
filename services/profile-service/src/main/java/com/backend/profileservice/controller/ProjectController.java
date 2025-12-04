package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.student.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.ProjectResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

//    private UUID getFakeUserId() {
//        // TODO: dùng JWT sau
//        return UUID.fromString("11111111-1111-1111-1111-111111111111");
//    }

    // POST /api/students/me/projects
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> create(@RequestHeader("X-User-Id") String userIdHeader,
                                                               @RequestBody ProjectCreateRequest request) {
        UUID userId = UUID.fromString(userIdHeader);
        ProjectResponse response = projectService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.PROJECT_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_CREATED.getCode(),
                        SuccessCode.PROJECT_CREATED.getMessage(),
                        response
                ));
    }

    // PUT /api/students/me/projects/{projectId}
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(@RequestHeader("X-User-Id") String userIdHeader,
                                                               @PathVariable("projectId") UUID projectId,
                                                               @RequestBody ProjectUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        ProjectResponse response = projectService.update(userId, projectId, request);

        return ResponseEntity
                .status(SuccessCode.PROJECT_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_UPDATED.getCode(),
                        SuccessCode.PROJECT_UPDATED.getMessage(),
                        response
                ));
    }

    // GET /api/students/me/projects
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAll(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        List<ProjectResponse> list = projectService.getAllByStudent(userId);

        return ResponseEntity
                .status(SuccessCode.PROJECT_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_FETCHED.getCode(),
                        SuccessCode.PROJECT_FETCHED.getMessage(),
                        list
                ));
    }

    // DELETE /api/students/me/projects/{projectId}
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestHeader("X-User-Id") String userIdHeader,
                                                    @PathVariable("projectId") UUID projectId) {
        UUID userId = UUID.fromString(userIdHeader);
        projectService.delete(userId, projectId);

        return ResponseEntity
                .status(SuccessCode.PROJECT_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_DELETED.getCode(),
                        SuccessCode.PROJECT_DELETED.getMessage(),
                        null
                ));
    }
}
