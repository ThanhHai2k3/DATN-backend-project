package com.backend.message_service.service;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;

import java.util.List;

public interface ConversationService {
    ConversationResponse findOrCreateConversation(FindConversationRequest request);
    List<ConversationResponse> getConversationsByUserId(Long userId);
}
