package com.backend.message_service.service;

import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.ReactionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(SendMessageRequest request);
    MessageResponse recallMessage(Long messageId, UUID userId);
//    Page<MessageResponse> getMessagesByConversation(Long conversationId, int pageNumber, int pageSize);
    List<MessageResponse> getMessagesByConversation(Long conversationId);
    ReactionResponse addReaction(CreateReactionRequest request);
    void removeReaction(Long reactionId, UUID userId);
}
