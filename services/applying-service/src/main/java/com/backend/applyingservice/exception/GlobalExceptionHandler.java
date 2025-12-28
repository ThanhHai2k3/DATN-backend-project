package com.backend.applyingservice.exception;

import com.backend.applyingservice.dto.external.ApiResponse;
import com.backend.applyingservice.enums.ErrorCode;
import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(ApiResponse.error(ErrorCode.VALIDATION_FAILED.getCode(), message));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParse(HttpMessageNotReadableException ex) {
        // Xử lý lỗi Enum hoặc JSON format sai
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("BAD_JSON", "Malformed JSON request or invalid Enum"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        return ResponseEntity
                .status(ErrorCode.SERVICE_UNAVAILABLE.getStatus())
                .body(ApiResponse.error(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "Error calling external service: " + ex.getMessage()));
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getStatus())
                .body(ApiResponse.error(ErrorCode.FORBIDDEN.getCode(), "Access denied"));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception exception){
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(), exception.getMessage()));
    }
}