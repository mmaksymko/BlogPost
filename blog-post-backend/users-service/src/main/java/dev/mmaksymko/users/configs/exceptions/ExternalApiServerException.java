package dev.mmaksymko.users.configs.exceptions;

public class ExternalApiServerException extends RuntimeException {
    public ExternalApiServerException(String errorMessage) {
        super(errorMessage);
    }
}
