//package com.backend.message_service.controller;
//
//import com.backend.message_service.dto.response.MessageResponse;
//import com.backend.message_service.dto.request.SendMessageRequest;
//import com.backend.message_service.service.MessageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController // Đánh dấu đây là một REST Controller, chuyên trả về JSON
//@RequestMapping("/api/v1/messages") // Tiền tố chung cho tất cả API trong file này
//@RequiredArgsConstructor
//public class MessageController {
//
//    private final MessageService messageService; // Tiêm "đầu bếp" vào
//
//    /**
//     * Endpoint để gửi một tin nhắn mới.
//     * @param request Dữ liệu được gửi từ client, chứa trong body của request.
//     * @return Dữ liệu của tin nhắn đã được tạo.
//     */
//    @PostMapping
//    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
//        // 1. Nhận "phiếu gọi món" (SendMessageRequest) từ client
//        // 2. Chuyển cho "đầu bếp" (MessageService) xử lý
//        MessageResponse response = messageService.sendMessage(request);
//        // 3. Nhận lại "món ăn đã hoàn thành" (MessageResponse) và trả về cho client
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Endpoint để lấy lịch sử tin nhắn của một cuộc trò chuyện.
//     * @param conversationId ID của cuộc trò chuyện, lấy từ đường dẫn URL.
//     * @param page Số thứ tự trang, lấy từ query param (ví dụ: ?page=0).
//     * @param size Kích thước trang, lấy từ query param (ví dụ: &size=20).
//     * @return Một trang (Page) chứa danh sách tin nhắn.
//     */
//    @GetMapping("/conversation/{conversationId}")
//    public ResponseEntity<Page<MessageResponse>> getMessagesByConversation(
//            @PathVariable Long conversationId,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "20") int size) {
//
//        Page<MessageResponse> messagePage = messageService.getMessagesByConversation(conversationId, page, size);
//        return ResponseEntity.ok(messagePage);
//    }
//}
package com.backend.message_service.controller;

import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    /**
    URL: http://localhost:8084/api/v1/messages   POST
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    /** GET
    http://localhost:8084/api/v1/messages/conversation/{conversationId}?page=0&size=20
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponse>> getMessagesByConversation(
            @PathVariable("conversationId") Long conversationId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<MessageResponse> messagePage = messageService.getMessagesByConversation( conversationId, page, size);
        return ResponseEntity.ok(messagePage);
    }
}