package dev.mmaksymko.images.configs.exceptions;

public class BucketCreationException extends RuntimeException {
    public BucketCreationException(String errorMessage) {
        super(errorMessage);
    }
}
