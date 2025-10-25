//package com.backend.message_service.service.impl;
//
//import com.backend.message_service.entity.Reaction;
//import com.backend.message_service.repository.ReactionRepository;
//import com.backend.message_service.service.ReactionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import lombok.Builder;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ReactionServiceImpl implements ReactionService {
//
//    private final ReactionRepository reactionRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Override
//    @Transactional
//    public String toggleReaction(Long messageId, Long userId, String type) {
//        Optional<Reaction> existing = reactionRepository.findByMessageIdAndUserId(messageId, userId);
//        String action;
//
////        if (existing.isPresent()) {
//            Reaction reaction = existing.get();
//            if (reaction.getReactionType().equalsIgnoreCase(type)) {
//                // nếu cùng loại => remove
//                reactionRepository.delete(reaction);
//                action = "removed";
//            } else {
//                // nếu khác loại => update
//                reaction.setReactionType(type);
//                reactionRepository.save(reaction);
//                action = "updated";
//            }
////        }
////        else {
////
//////            // nếu chưa có => add
//////            Reaction newReaction = Reaction.builder()
//////                    .message(messageId)
//////                    .user(userId)
//////                    .type(type)
//////                    .build();
//////            reactionRepository.save(newReaction);
//////            action = "added";
////        }
//        // TODO: Sửa lại Reaction Entity sao cho tên biến mapping với tên column
//
//        // gửi realtime qua socket
//        String destination = "/topic/message/" + messageId + "/reactions";
//        messagingTemplate.convertAndSend(destination, getReactions(messageId));
//        System.out.println("Đã gửi cập nhật reaction đến: " + destination);
//
//        return action;
//    }
//
//    @Override
//    public List<Reaction> getReactions(Long messageId) {
//        return reactionRepository.findByMessageId(messageId);
//    }
//}
