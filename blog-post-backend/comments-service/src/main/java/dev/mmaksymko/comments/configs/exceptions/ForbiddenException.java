package dev.mmaksymko.comments.configs.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}