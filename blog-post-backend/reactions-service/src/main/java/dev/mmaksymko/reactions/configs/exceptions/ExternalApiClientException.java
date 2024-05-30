package dev.mmaksymko.reactions.configs.exceptions;

public class ExternalApiClientException extends RuntimeException {
    public ExternalApiClientException(String errorMessage) {
        super(errorMessage);
    }
}