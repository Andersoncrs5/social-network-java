package com.blog.writeapi.modules.stories.model;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.AttachmentBaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "stories", indexes = {
        @Index(name = "idx_story_user_expiry", columnList = "user_id, expires_at")
})
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StoryModel extends AttachmentBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Builder.Default
    private boolean isArchived = false;

    @Column(length = 500)
    private String caption;

    @Column(name = "background_color", length = 12)
    private String backgroundColor;

    public boolean isActive() {
        return !isArchived && OffsetDateTime.now().isBefore(expiresAt);
    }
}