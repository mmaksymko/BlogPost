package dev.mmaksymko.users.configs.exceptions;

public class ResourceGoneException extends RuntimeException {
    public ResourceGoneException(String errorMessage) {
        super(errorMessage);
    }
}
