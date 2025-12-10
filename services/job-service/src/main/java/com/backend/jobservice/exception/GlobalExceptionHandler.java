package com.backend.jobservice.exception;

import com.backend.jobservice.dto.response.ApiResponse;
import com.backend.jobservice.enums.ErrorCode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
        // Lấy lỗi đầu tiên trong danh sách binding
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getCode(),
                        message
                ));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParse(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        // Case 1: ENUM không hợp lệ
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {

            InvalidFormatException ife = (InvalidFormatException) cause;

            // kiểm tra xem có phải lỗi enum hay không
            if (ife.getTargetType().isEnum()) {
                String allowed = Arrays.toString(ife.getTargetType().getEnumConstants());
                return ResponseEntity
                        .status(ErrorCode.INVALID_ENUM.getStatus())
                        .body(ApiResponse.error(
                                ErrorCode.INVALID_ENUM.getCode(),
                                "Invalid enum value. Allowed values: " + allowed
                        ));
            }
        }

        // Case 2: JSON sai format (ví dụ: thiếu dấu } )
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "BAD_JSON",
                        "Malformed JSON request"
                ));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(ErrorCode.INVALID_UUID.getStatus())
                .body(ApiResponse.error(
                        ErrorCode.INVALID_UUID.getCode(),
                        "Invalid UUID format: " + ex.getValue()
                ));
    }

    @ExceptionHandler(value = FeignException.NotFound.class)
    public ResponseEntity<ApiResponse<?>> handleFeignNotFound(FeignException.NotFound ex){
        return ResponseEntity
                .status(ErrorCode.SKILL_NOT_FOUND.getStatus())
                .body(ApiResponse.error(
                        ErrorCode.SKILL_NOT_FOUND.getCode(),
                        ErrorCode.SKILL_NOT_FOUND.getMessage()
                ));

    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        return ResponseEntity
                .status(ErrorCode.SERVICE_UNAVAILABLE.getStatus())
                .body(ApiResponse.error(
                        ErrorCode.SERVICE_UNAVAILABLE.getCode(),
                        ErrorCode.SERVICE_UNAVAILABLE.getMessage()
                ));
    }

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getStatus())
                .body(ApiResponse.error(
                        ErrorCode.FORBIDDEN.getCode(),
                        "Access denied for this action"
                ));
    }


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception exception){
        ErrorCode errorCode =ErrorCode.INTERNAL_ERROR;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage()
                ));
    }
}
