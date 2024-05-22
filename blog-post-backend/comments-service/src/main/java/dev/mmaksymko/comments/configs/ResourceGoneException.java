package dev.mmaksymko.comments.configs;

public class ResourceGoneException extends RuntimeException {
    public ResourceGoneException(String errorMessage) {
        super(errorMessage);
    }
}
