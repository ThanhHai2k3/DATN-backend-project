package com.backend.message_service.controller;

import com.backend.message_service.dto.response.ConversationResponse;
import com.backend.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponse>> getConversationsByUserId(@PathVariable("userId") UUID userId) {
        List<ConversationResponse> responses = conversationService.getConversationsByUserId(userId);
        return ResponseEntity.ok(responses);
    }
}
