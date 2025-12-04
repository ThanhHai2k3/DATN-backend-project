package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.student.StudentSkillResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/v2/students/me/skills")
@RequiredArgsConstructor
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

//    private UUID getFakeUserId() {
//        return UUID.fromString("11111111-1111-1111-1111-111111111111");
//    }

    // POST /api/students/me/skills
    @PostMapping
    public ResponseEntity<ApiResponse<StudentSkillResponse>> create(@RequestHeader("X-User-Id") String userIdHeader,
                                                                    @RequestBody StudentSkillCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        StudentSkillResponse response = studentSkillService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_CREATED.getCode(),
                        SuccessCode.STUDENT_SKILL_CREATED.getMessage(),
                        response
                ));
    }

    // Update skill level/years/note
    // PUT /api/students/me/skills/{skillId}
    @PutMapping("/{studentSkillId}")
    public ResponseEntity<ApiResponse<StudentSkillResponse>> update(@RequestHeader("X-User-Id") String userIdHeader,
                                                                    @PathVariable("studentSkillId") UUID studentSkillId,
                                                                    @RequestBody StudentSkillUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        StudentSkillResponse response = studentSkillService.update(userId, studentSkillId, request);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_UPDATED.getCode(),
                        SuccessCode.STUDENT_SKILL_UPDATED.getMessage(),
                        response
                ));
    }

    // GET /api/students/me/skills
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentSkillResponse>>> getAll(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        List<StudentSkillResponse> list = studentSkillService.getAllByStudent(userId);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_FETCHED.getCode(),
                        SuccessCode.STUDENT_SKILL_FETCHED.getMessage(),
                        list
                ));
    }

    // DELETE /api/students/me/skills/{skillId}
    @DeleteMapping("/{studentSkillId}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestHeader("X-User-Id") String userIdHeader,
                                                    @PathVariable("studentSkillId") UUID studentSkillId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        studentSkillService.delete(userId, studentSkillId);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_DELETED.getCode(),
                        SuccessCode.STUDENT_SKILL_DELETED.getMessage(),
                        null
                ));
    }
}
