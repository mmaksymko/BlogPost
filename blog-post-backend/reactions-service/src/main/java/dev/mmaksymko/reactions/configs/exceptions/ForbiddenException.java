package dev.mmaksymko.reactions.configs.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}