package com.blog.writeapi.modules.userSettings.dto;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.userSettings.model.enums.ContentFilterLevelEnum;
import com.blog.writeapi.modules.userSettings.model.enums.FontSizeScaleEnum;
import com.blog.writeapi.modules.userSettings.model.enums.LanguageEnum;
import com.blog.writeapi.modules.userSettings.model.enums.ThemeEnum;

import java.time.OffsetDateTime;

public record UserSettingsDTO(
        Long id,
        LanguageEnum language,
        ThemeEnum theme,
        boolean showOnlineStatus,
        boolean notifyNewFollower,
        boolean notifyLikes,
        boolean notifyComments,
        boolean notifyMentions,
        boolean twoFactorEnabled,
        boolean autoplayVideos,
        boolean marketingEmailsAllowed,
        int itemsPerPage,
        ContentFilterLevelEnum contentFilterLevel,
        FontSizeScaleEnum fontSizeScale,
        String timezone,
        UserDTO user,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
