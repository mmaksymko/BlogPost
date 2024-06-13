package dev.mmaksymko.users.configs.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}