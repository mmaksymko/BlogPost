package dev.mmaksymko.users.configs.exceptions;

public class ExternalApiClientException extends RuntimeException {
    public ExternalApiClientException(String errorMessage) {
        super(errorMessage);
    }
}