package dev.mmaksymko.comments.configs;

import dev.mmaksymko.comments.configs.exceptions.ExternalApiClientException;
import dev.mmaksymko.comments.configs.exceptions.ExternalApiServerException;
import dev.mmaksymko.comments.configs.exceptions.ResourceGoneException;
import dev.mmaksymko.comments.dto.ErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ResourceGoneException.class)
    public ResponseEntity<ErrorResponse> handleException(ResourceGoneException e) {
        return ResponseEntity.status(HttpStatus.GONE).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ExternalApiServerException.class)
    public ResponseEntity<ErrorResponse> handleException(ExternalApiServerException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ExternalApiClientException.class)
    public ResponseEntity<ErrorResponse> handleException(ExternalApiClientException e) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> handleException(RequestNotPermitted e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MaxRetriesExceededException.class)
    public ResponseEntity<ErrorResponse> handleException(MaxRetriesExceededException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
}