package com.backend.applyingservice.dto.external;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(String code, String message, T data){
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message){
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}