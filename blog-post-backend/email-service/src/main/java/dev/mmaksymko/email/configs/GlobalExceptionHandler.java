package dev.mmaksymko.email.configs;

import dev.mmaksymko.email.dto.ErrorResponse;
import jakarta.mail.MessagingException;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleException(KafkaException e) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
}