package com.backend.message_service.controller;

import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.response.ReactionResponse;
import com.backend.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/message/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {
    //TODO: controller này bỏ nhé, dư thời gian thì mới làm
    private final MessageService messageService;


    @PostMapping
    @PreAuthorize("hasAuthorized()")
    public ResponseEntity<ReactionResponse> addReaction(
            @AuthenticationPrincipal UUID currentUserId,
            @RequestBody CreateReactionRequest request
    ) {
        return ResponseEntity.ok(messageService.addReaction(currentUserId, request));
    }


    @DeleteMapping("/{reactionId}")
    @PreAuthorize("hasAuthorized()")
    public ResponseEntity<Void> removeReaction(
            @PathVariable("reactionId") Long reactionId,
            @AuthenticationPrincipal UUID userId) { // userId sẽ lấy từ token sau
        messageService.removeReaction(reactionId, userId);
        return ResponseEntity.noContent().build();
    }
}