package com.backend.message_service.controller;

import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.enums.MessageType;
import com.backend.message_service.service.MessageService;
import com.backend.message_service.service.S3FileStorageService; // <-- 1. Import service S3
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // <-- Import MultipartFile

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final S3FileStorageService s3FileStorageService;

    /**
     * Endpoint để gửi tin nhắn TEXT.
     * URL: POST http://localhost:8084/api/v1/messages
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    // === TẠO ENDPOINT MỚI ĐỂ GỬI ẢNH ===
    /**
     * Endpoint để gửi tin nhắn dạng ẢNH.
     * Client sẽ gửi request dạng multipart/form-data.
     * URL: POST http://localhost:8084/api/v1/messages/image
     */
    @PostMapping("/image")
    public ResponseEntity<?> sendImageMessage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("conversationId") Long conversationId,
            @RequestParam("senderId") UUID senderId) {

        try {
            String fileUrl = s3FileStorageService.storeFile(file);


            SendMessageRequest imageRequest = new SendMessageRequest();
            imageRequest.setConversationId(conversationId);
            imageRequest.setSenderId(senderId);
            imageRequest.setContent(fileUrl);

            imageRequest.setMessageType(String.valueOf(MessageType.IMAGE));

            messageService.sendMessage(imageRequest);

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: Không thể tải ảnh lên. " + e.getMessage());
        }
    }


    /**
     * Endpoint để lấy lịch sử tin nhắn.
     * URL: GET http://localhost:8084/api/v1/messages/conversation/{conversationId}?page=0&size=20
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByConversation(@PathVariable("conversationId") Long conversationId) {
        List<MessageResponse> messages = messageService.getMessagesByConversation(conversationId);
        return ResponseEntity.ok(messages);
    }
}