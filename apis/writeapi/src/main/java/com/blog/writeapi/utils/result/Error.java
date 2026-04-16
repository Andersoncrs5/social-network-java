package com.blog.writeapi.utils.result;

public record Error(
        String code,
        String message,
        int status
) {
    public static final Error NONE = new Error("", "", 200);
}