package dev.mmaksymko.reactions.configs.exceptions;

import dev.mmaksymko.reactions.dto.ErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
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

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleException(ServerWebInputException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleException(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
}