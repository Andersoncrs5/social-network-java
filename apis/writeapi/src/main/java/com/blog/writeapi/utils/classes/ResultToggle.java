package com.blog.writeapi.utils.classes;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.blog.writeapi.utils.enums.global.ToggleEnum;

import java.util.Optional;

public record ResultToggle<T extends BaseEntity>(
        Optional<T> body,
        ToggleEnum result
) {
    public static <T extends BaseEntity > ResultToggle<T> added(T body) {
        return new ResultToggle<>(Optional.of(body), ToggleEnum.ADDED);
    }

    public static <T extends BaseEntity > ResultToggle<T> removed() {
        return new ResultToggle<>(Optional.empty(), ToggleEnum.REMOVED);
    }

    public static <T extends BaseEntity > ResultToggle<T> updated(T body) {return new ResultToggle<>(Optional.of(body), ToggleEnum.UPDATED);}
}
