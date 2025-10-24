package com.backend.message_service.service.impl;

import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.UserResponse;
import com.backend.message_service.entity.Conversation;
import com.backend.message_service.entity.Message;
import com.backend.message_service.entity.MessageType;
import com.backend.message_service.entity.User;
import com.backend.message_service.repository.ConversationRepository;
import com.backend.message_service.repository.MessageRepository;
import com.backend.message_service.repository.UserRepository;
import com.backend.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc trò chuyện với ID: " + request.getConversationId()));
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + request.getSenderId()));


        Message newMessage = new Message();
        newMessage.setConversation(conversation);
        newMessage.setSender(sender);
        newMessage.setContent(request.getContent());
        newMessage.setMessageType(MessageType.valueOf(request.getMessageType().toUpperCase()));


        Message savedMessage = messageRepository.save(newMessage);


        conversation.setLastMessageId(savedMessage.getId());
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);


//        return convertToMessageResponse(savedMessage);
        MessageResponse response = convertToMessageResponse(savedMessage);


        // gửi res  đến kênh "/topic/conversation/{conversationId}"
        String destination = "/topic/conversation/" + response.getConversationId();
        messagingTemplate.convertAndSend(destination, response);
        System.out.println("Đã đẩy tin nhắn đến kênh: " + destination);

        return response;
    }

    @Override
    @Transactional
    public MessageResponse recallMessage(Long messageId, Long userId) {

        Message messageToRecall = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin nhắn với ID: " + messageId));


        if (!messageToRecall.getSender().getId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền thu hồi tin nhắn này.");
        }

        messageToRecall.setMessageType(MessageType.RECALLED);
        messageToRecall.setContent(""); //có nên xóa hẳn nd tin nhắn k?

        Message recalledMessage = messageRepository.save(messageToRecall);

        return convertToMessageResponse(recalledMessage);
    }

    @Override
    public Page<MessageResponse> getMessagesByConversation(Long conversationId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sentAt").descending());
        Page<Message> messagePage = messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable);
        return messagePage.map(this::convertToMessageResponse);
    }

    private MessageResponse convertToMessageResponse(Message message) {
        if (message == null) return null;

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversation().getId());


        if (message.getMessageType() == MessageType.RECALLED) {
            response.setContent("");
        } else {
            response.setContent(message.getContent());
        }

        response.setMessageType(message.getMessageType().name());
        response.setSentAt(message.getSentAt());


        User sender = message.getSender();
        UserResponse senderResponse = new UserResponse();
        senderResponse.setId(sender.getId());
        senderResponse.setFullName(sender.getFullName());
        senderResponse.setAvatarUrl(sender.getAvatarUrl());
        response.setSender(senderResponse);

        // TODO: Lấy và chuyển đổi danh sách reactions sau này

        return response;
    }
}