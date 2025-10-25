package com.backend.message_service.service;

import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.ReactionResponse;
import org.springframework.data.domain.Page;

public interface MessageService {
    MessageResponse sendMessage(SendMessageRequest request);
    MessageResponse recallMessage(Long messageId, Long userId);
    Page<MessageResponse> getMessagesByConversation(Long conversationId, int pageNumber, int pageSize);
    ReactionResponse addReaction(CreateReactionRequest request);
    void removeReaction(Long reactionId, Long userId);
}
