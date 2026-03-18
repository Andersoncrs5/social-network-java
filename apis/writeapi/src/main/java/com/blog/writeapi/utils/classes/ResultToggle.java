package com.blog.writeapi.utils.classes;

import com.blog.writeapi.utils.enums.global.ToggleEnum;

import java.util.Optional;

public record ResultToggle<T>(
        Optional<T> body,
        ToggleEnum result
) {
}
