package com.blog.writeapi.utils.enums.attachment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum AttachmentTypeEnum {
    IMAGE("image", "Static visual content"),
    VIDEO("video", "Short-form motion content"),
    TEXT("text", "Text-based background content");

    private final String code;
    private final String description;

    public boolean isVisual() {
        return this == IMAGE || this == VIDEO;
    }
}
