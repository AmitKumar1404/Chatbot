package com.chatbot.exception;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import static com.chatbot.constant.AppConstants.INVALID_USERNAME_OR_PASSWORD;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(ResponseCode.NOT_FOUND)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ResponseCode.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .message(INVALID_USERNAME_OR_PASSWORD)
                .status(ResponseCode.UNAUTHORIZED)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ResponseCode.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "Bad request")
                .status(ResponseCode.BAD_REQUEST)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ResponseCode.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        String message = isInvalidFeedbackReason(ex)
                ? "feedbackReason must be one of: INCORRECT, INCOMPLETE, HALLUCINATION, OFFENSIVE, OTHER"
                : "Invalid request body";
        ErrorResponse body = ErrorResponse.builder()
                .message(message)
                .status(ResponseCode.BAD_REQUEST)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ResponseCode.BAD_REQUEST).body(body);
    }

    private boolean isInvalidFeedbackReason(HttpMessageNotReadableException ex) {
        if (!(ex.getCause() instanceof InvalidFormatException invalidFormatException)) {
            return false;
        }
        return invalidFormatException.getPath().stream()
                .anyMatch(reference -> "feedbackReason".equals(reference.getFieldName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .status(ResponseCode.INTERNAL_SERVER_ERROR)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR).body(body);
    }
}
