package dev.mmaksymko.comments.configs.exceptions;

public class ExternalApiServerException extends RuntimeException {
    public ExternalApiServerException(String errorMessage) {
        super(errorMessage);
    }
}
