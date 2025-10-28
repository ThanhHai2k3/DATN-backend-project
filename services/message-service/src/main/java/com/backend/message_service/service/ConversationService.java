package com.backend.message_service.service;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {
    ConversationResponse findOrCreateConversation(FindConversationRequest request);
    List<ConversationResponse> getConversationsByUserId(UUID userId);
}
