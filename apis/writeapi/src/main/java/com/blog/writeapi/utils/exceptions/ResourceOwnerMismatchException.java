package com.blog.writeapi.utils.exceptions;

public class ResourceOwnerMismatchException extends RuntimeException {
    public ResourceOwnerMismatchException(String message) {
        super(message);
    }
}
