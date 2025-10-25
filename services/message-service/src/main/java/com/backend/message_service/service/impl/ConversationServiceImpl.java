package com.backend.message_service.service.impl;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.response.UserResponse;
import com.backend.message_service.entity.Conversation;
import com.backend.message_service.entity.Message;
import com.backend.message_service.entity.MessageType;
import com.backend.message_service.entity.User;
import com.backend.message_service.repository.ConversationRepository;
import com.backend.message_service.repository.MessageRepository;
import com.backend.message_service.repository.UserRepository;
import com.backend.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    // (Sau này cần MessageRepository và Mapper)

    @Transactional
    public ConversationResponse findOrCreateConversation(FindConversationRequest request) {
        // Sắp xếp ID để đảm bảo user1Id < user2Id
        Long user1Id = Math.min(request.getUser1Id(), request.getUser2Id());
        Long user2Id = Math.max(request.getUser1Id(), request.getUser2Id());

        // Tìm kiếm cuộc trò chuyện
        Conversation conversation = conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id)
                .orElseGet(() -> {
                    //user1 và user2 là 2 user trong đoạn chat
                    User user1 = userRepository.findById(user1Id).orElseThrow(()->new RuntimeException("Không tin thấy người dùng với id = "+user1Id.toString()));
                    User user2 = userRepository.findById(user2Id).orElseThrow(()->new RuntimeException("Không tin thấy người dùng với id = "+user2Id.toString()));

                    Conversation newConversation = new Conversation();
                    newConversation.setUser1(user1);
                    newConversation.setUser2(user2);
                    return conversationRepository.save(newConversation);
                });

        // Chuyển đổi sang DTO để trả về (cần viết hàm convert)
        return convertToConversationResponse(conversation, request.getUser1Id());
    }

    private ConversationResponse convertToConversationResponse(Conversation conversation, Long user1Id) {
        if(conversation == null) return null;
        ConversationResponse res = new ConversationResponse();
        res.setId(conversation.getId());

        //receiver là người nhận tin nhắn
        User receiver = conversation.getUser1().equals(user1Id) ? conversation.getUser2() : conversation.getUser1();

        UserResponse receiverResponse = new UserResponse();
        receiverResponse.setId(receiver.getId());
        receiverResponse.setFullName(receiver.getFullName());
        receiverResponse.setAvatarUrl(receiver.getAvatarUrl());
        res.setReceiver(receiverResponse);

//        res.setLastMessage(convertToMessageResponse(messageRepository.findById(conversation.getLastMessageId())));
        if(conversation.getLastMessageId()!=null){
            Message lastMesEntity = messageRepository.findById(conversation.getLastMessageId()).orElse(null);
            res.setLastMessage(convertToMessageResponse(lastMesEntity));
        }
        else{
            res.setLastMessage(null);
        }
        res.setUnreadCount(0); // TODO: Triển khai logic đếm tin nhắn chưa đọc sau
        return res;
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

        // TODO: Lấy và chuyển đổi danh sách reactions sau này, copy từ messageService, sau có thời gian thì triển khai mapper và xóa hàm này đi
        return response;
    }

    @Transactional
    public List<ConversationResponse> getConversationsByUserId(Long userId){
        List<Conversation> listConversationRepo = conversationRepository.findByUser1IdOrUser2IdOrderByUpdatedAtDesc(userId, userId);
        List<ConversationResponse> res = new ArrayList<>();
        for(Conversation x:listConversationRepo)
            res.add(convertToConversationResponse(x, userId));
        return res;
    };

}