package dev.mmaksymko.comments.configs.exceptions;

public class ResourceGoneException extends RuntimeException {
    public ResourceGoneException(String errorMessage) {
        super(errorMessage);
    }
}
