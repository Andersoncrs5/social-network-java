package com.blog.writeapi.modules.userSettings.model;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userSettings.model.enums.ContentFilterLevelEnum;
import com.blog.writeapi.modules.userSettings.model.enums.FontSizeScaleEnum;
import com.blog.writeapi.modules.userSettings.model.enums.LanguageEnum;
import com.blog.writeapi.modules.userSettings.model.enums.ThemeEnum;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "user_settings",
        indexes = {
            @Index(name = "idx_user_id_user_settings", columnList = "user_id", unique = true)
        }
)
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsModel extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private LanguageEnum language = LanguageEnum.NONE;

    @Enumerated(EnumType.STRING)
    private ThemeEnum theme = ThemeEnum.SYSTEM;

    private boolean showOnlineStatus = true;

    @Builder.Default
    private boolean notifyNewFollower = true;

    @Builder.Default
    private boolean notifyComments = true;

    @Builder.Default
    private boolean notifyLikes = true;

    @Builder.Default
    private boolean notifyMentions = true;
    private boolean twoFactorEnabled = false;
    private boolean autoplayVideos = true;
    private int itemsPerPage = 10;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "content_filter_level", length = 20, nullable = false)
    private ContentFilterLevelEnum contentFilterLevel = ContentFilterLevelEnum.MODERATE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "font_size_scale", length = 20, nullable = false)
    private FontSizeScaleEnum fontSizeScale = FontSizeScaleEnum.MEDIUM;

    @Builder.Default
    @Column(name = "marketing_emails_allowed", nullable = false)
    private boolean marketingEmailsAllowed = false;

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;
}