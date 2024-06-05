package dev.mmaksymko.gateway.configs.exceptions;

public class ExternalApiServerException extends RuntimeException {
    public ExternalApiServerException(String errorMessage) {
        super(errorMessage);
    }
}
