package com.blog.writeapi.utils.classes;

import com.blog.writeapi.utils.enums.global.ToggleEnum;

import java.util.Optional;

public record ResultToggle<T>(
        Optional<T> body,
        ToggleEnum result
) {
    public static <T> ResultToggle<T> added(T body) {
        return new ResultToggle<>(Optional.of(body), ToggleEnum.ADDED);
    }

    public static <T> ResultToggle<T> removed() {
        return new ResultToggle<>(Optional.empty(), ToggleEnum.REMOVED);
    }
}
