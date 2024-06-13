package dev.mmaksymko.blogpost.configs.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}