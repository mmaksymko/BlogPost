package dev.mmaksymko.comments.configs.exceptions;

public class ExternalApiClientException extends RuntimeException {
    public ExternalApiClientException(String errorMessage) {
        super(errorMessage);
    }
}