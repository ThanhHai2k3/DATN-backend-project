package com.backend.message_service.service.impl;

import com.backend.message_service.dto.WebSocketEvent;
import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.ReactionResponse;
import com.backend.message_service.dto.response.UserResponse;
import com.backend.message_service.entity.*;
import com.backend.message_service.enums.MessageType;
import com.backend.message_service.enums.ReactionType;
import com.backend.message_service.repository.ConversationRepository;
import com.backend.message_service.repository.MessageRepository;
import com.backend.message_service.repository.ReactionRepository;
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
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReactionRepository reactionRepository;

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


        // gửi res đến kênh "/topic/conversation/{conversationId}"
        String destination = "/topic/conversation/" + response.getConversationId();
        messagingTemplate.convertAndSend(destination, response);
        System.out.println("Đã đẩy tin nhắn đến kênh: " + destination);

        return response;
    }

    @Override
    @Transactional
    public MessageResponse recallMessage(Long messageId, UUID userId) {

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
    @Transactional
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
        List<Reaction> reactions = reactionRepository.findByMessageId(message.getId());
        if (reactions != null && !reactions.isEmpty()) {
            response.setReactions(
                    reactions.stream()
                            .map(this::convertToReactionResponse)
                            .collect(Collectors.toList())
            );
        } else {
            response.setReactions(Collections.emptyList());
        }

        return response;
    }
//    public ReactionResponse addReaction(CreateReactionRequest request){
//        Message message = messageRepository.findById(request.getMessageId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
//
//        // 2. Tạo và lưu Reaction Entity
//        Reaction newReaction = new Reaction();
//        newReaction.setMessage(message);
//        newReaction.setUser(user);
//        newReaction.setReactionType(request.getReactionType());
//        Reaction savedReaction = reactionRepository.save(newReaction);
//
//        // 3. Chuyển đổi sang DTO
//        ReactionResponse response = convertToReactionResponse(savedReaction);
//
//        // 4. Đẩy sự kiện "ADD_REACTION" qua WebSocket
//        String destination = "/topic/conversation/" + message.getConversation().getId();
//        WebSocketEvent<ReactionResponse> event = new WebSocketEvent<>("ADD_REACTION", response);
//        messagingTemplate.convertAndSend(destination, event);
//
//        return response;
//    }
@Override
@Transactional
public ReactionResponse addReaction(CreateReactionRequest request) {
    // 1. Lấy các Entity liên quan từ database
    Message message = messageRepository.findById(request.getMessageId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn với ID: " + request.getMessageId()));
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + request.getUserId()));

    // 2. Tạo và lưu Reaction Entity
    Reaction newReaction = new Reaction();
    newReaction.setMessage(message);
    newReaction.setUser(user);

    // --- THAY ĐỔI QUAN TRỌNG: CHUYỂN ĐỔI TỪ STRING SANG ENUM ---
    // Chuyển chuỗi String từ request (ví dụ: "LIKE") thành giá trị Enum ReactionType.LIKE
    // toUpperCase() để đảm bảo khớp với tên Enum (LIKE, LOVE...)
    newReaction.setReactionType(ReactionType.valueOf(request.getReactionType().toUpperCase()));

    Reaction savedReaction = reactionRepository.save(newReaction);

    // 3. Chuyển đổi sang DTO để trả về
    ReactionResponse response = convertToReactionResponse(savedReaction);

    // 4. Đẩy sự kiện "ADD_REACTION" qua WebSocket
    String destination = "/topic/conversation/" + message.getConversation().getId();
    WebSocketEvent<ReactionResponse> event = new WebSocketEvent<>("ADD_REACTION", response);
    messagingTemplate.convertAndSend(destination, event);

    return response;
}

//    private ReactionResponse convertToReactionResponse(Reaction savedReaction) {
//        if(savedReaction.getId()==null || savedReaction.getReactionType()==null) return null;
//        ReactionResponse res = new ReactionResponse();
//        res.setId(savedReaction.getId());
//        res.setUserId(savedReaction.getUser().getId());
//        res.setReactionType(savedReaction.getReactionType());
//        res.setMessageId(savedReaction.getMessage().getId());
//        return res;
//    }
private ReactionResponse convertToReactionResponse(Reaction reaction) {
    if (reaction == null) return null;
    ReactionResponse response = new ReactionResponse();
    response.setId(reaction.getId());
    response.setMessageId(reaction.getMessage().getId());
    response.setUserId(reaction.getUser().getId());
    response.setReactionType(reaction.getReactionType().name());
    return response;
}
    @Override
    @Transactional
    public void removeReaction(Long reactionId, UUID userId){
        Reaction reaction = reactionRepository.findById(reactionId).orElseThrow(()-> new RuntimeException("Reaction không tồn tại!"));
        if(!reaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xóa reaction này.");
//            return;
        }
        Long conversationId = reaction.getMessage().getConversation().getId();
        Long messageId = reaction.getMessage().getId();
        reactionRepository.deleteById(reactionId);

        String destination = "/topic/conversation/" + conversationId;
        Map<String, Long> payload = new HashMap<>();
        payload.put("messageId", messageId);
        payload.put("reactionId", reactionId);
        WebSocketEvent<Map<String, Long>> event = new WebSocketEvent<>("REMOVE_REACTION", payload);
        messagingTemplate.convertAndSend(destination, event);
    }
}