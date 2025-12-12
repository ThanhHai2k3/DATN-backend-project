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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UUID senderId,
            @RequestBody SendMessageRequest request
    ) {
        return ResponseEntity.ok(messageService.sendMessage(senderId, request));
    }

    // === TẠO ENDPOINT MỚI ĐỂ GỬI ẢNH ===
    /**
     * Endpoint để gửi tin nhắn dạng ẢNH.
     * Client sẽ gửi request dạng multipart/form-data.
     * URL: POST http://localhost:8084/api/v1/messages/image
     */
    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendImageMessage(
            @AuthenticationPrincipal UUID senderId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("conversationId") Long conversationId
    ) {
        try {
            String fileUrl = s3FileStorageService.storeFile(file);

            SendMessageRequest imageRequest = new SendMessageRequest();
            imageRequest.setConversationId(conversationId);
            imageRequest.setContent(fileUrl);
            imageRequest.setMessageType(String.valueOf(MessageType.IMAGE));

//            messageService.sendMessage(senderId, imageRequest);
//            return ResponseEntity.ok().build();
            MessageResponse saved = messageService.sendMessage(senderId, imageRequest);
            return ResponseEntity.ok(saved);


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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getMessagesByConversation(
            @PathVariable("conversationId") Long conversationId,
            @AuthenticationPrincipal UUID senderID) {
        List<MessageResponse> messages = messageService.getMessagesByConversation(senderID, conversationId);
        return ResponseEntity.ok(messages);
    }
}