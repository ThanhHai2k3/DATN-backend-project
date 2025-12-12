package com.backend.message_service.controller;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;
import com.backend.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
//    @GetMapping("/user/{userId}")
    @GetMapping("/my-conversation/{userId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<ConversationResponse>> getConversationsByUserId(@PathVariable("userId") UUID userId) {
        List<ConversationResponse> responses = conversationService.getConversationsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/newConversation") //cái này để lấy 1 conversation đã có từ trước hoặc là tạo 1 conversation mới
    @PreAuthorize("hasAuthority('STUDENT')")
    public ConversationResponse findOrCreateConversation(
            @AuthenticationPrincipal UUID UID1,
            @RequestBody FindConversationRequest request
    ) {
        return conversationService.findOrCreateConversation(UID1, request);
    }

}
