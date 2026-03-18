package com.blog.writeapi.utils.exceptions;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message, null, false, false);
    }
}
