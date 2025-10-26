package com.backend.message_service.controller;

import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.response.ReactionResponse;
import com.backend.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final MessageService messageService; // Hoặc ReactionService

    @PostMapping
    public ResponseEntity<ReactionResponse> addReaction(@RequestBody CreateReactionRequest request) {
        return ResponseEntity.ok(messageService.addReaction(request));
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<Void> removeReaction(@PathVariable("reactionId") Long reactionId, @RequestParam("userId") Long userId) { // userId sẽ lấy từ token sau
        messageService.removeReaction(reactionId, userId);
        return ResponseEntity.noContent().build();
    }
}