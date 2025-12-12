package com.backend.message_service.service.impl;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.response.UserResponse;
import com.backend.message_service.entity.Conversation;
import com.backend.message_service.entity.Message;
import com.backend.message_service.enums.MessageType;
//import com.backend.message_service.entity.User;
import com.backend.message_service.repository.ConversationRepository;
import com.backend.message_service.repository.MessageRepository;
//import com.backend.message_service.repository.UserRepository;
import com.backend.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
//    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    // (Sau này cần MessageRepository và Mapper)
    @Override
    @Transactional
    public ConversationResponse findOrCreateConversation(UUID UID1, FindConversationRequest request) {

        UUID other = request.getUser2Id();
        UUID min = UID1.compareTo(other) <= 0 ? UID1 : other;
        UUID max = UID1.compareTo(other) <= 0 ? other : UID1;

        request.setUser1Id(min);
        request.setUser2Id(max);

        Optional<Conversation> opt =
                conversationRepository.findByUser1IdAndUser2Id(request.getUser1Id(), request.getUser2Id());

        if (opt.isPresent()) {
            return convertToConversationResponse(opt.get(), UID1);
        }

        Conversation newConversation = new Conversation();
        newConversation.setUser1Id(request.getUser1Id());
        newConversation.setUser2Id(request.getUser2Id());

        Conversation saved = conversationRepository.save(newConversation);
        return convertToConversationResponse(saved, UID1);
    }


    private ConversationResponse convertToConversationResponse(Conversation conversation, UUID currentUserId) {
        ConversationResponse res = new ConversationResponse();
        res.setId(conversation.getId());

        UUID receiverId = conversation.getUser1Id().equals(currentUserId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(receiverId);
        // fullName/avatar FE tự fetch từ profile-service
        res.setReceiver(userResponse);

        if (conversation.getLastMessageId() != null) {
            Message last = messageRepository.findById(conversation.getLastMessageId()).orElse(null);
            res.setLastMessage(convertToMessageResponse(last));
        } else {
            res.setLastMessage(null);
        }

        res.setUnreadCount(0);
        return res;
    }


    private MessageResponse convertToMessageResponse(Message message) {
        if (message == null) return null;

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversation().getId());

        response.setContent(message.getMessageType() == MessageType.RECALLED ? "" : message.getContent());
        response.setMessageType(message.getMessageType().name());
        response.setSentAt(message.getSentAt());

        UserResponse senderResponse = new UserResponse();
        senderResponse.setId(message.getConversation().getUser1Id()); // UUID // đang ????
        response.setSenderId(message.getSenderId());

        return response;
    }


    @Transactional
    public List<ConversationResponse> getConversationsByUserId(UUID userId){
        List<Conversation> listConversationRepo = conversationRepository.findByUser1IdOrUser2IdOrderByUpdatedAtDesc(userId, userId);
        List<ConversationResponse> res = new ArrayList<>();
        for(Conversation x:listConversationRepo)
            res.add(convertToConversationResponse(x, userId));
        return res;
    };

}