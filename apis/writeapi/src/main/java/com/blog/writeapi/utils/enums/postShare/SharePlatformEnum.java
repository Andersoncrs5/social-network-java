package com.blog.writeapi.utils.enums.postShare;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SharePlatformEnum {
    WHATSAPP("WhatsApp", "mobile"),
    TELEGRAM("Telegram", "mobile"),
    TWITTER_X("Twitter/X", "web"),
    FACEBOOK("Facebook", "web"),
    LINKEDIN("LinkedIn", "professional"),
    REDDIT("Reddit", "community"),
    LINK_COPY("Link Copy", "direct"),
    SYSTEM_SHARE("Native System", "mobile_native");

    private final String description;
    private final String category;

}