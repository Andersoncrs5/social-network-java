package com.blog.writeapi.modules.postShare.model;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "post_shares",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_shares", columnNames = {"post_id", "user_id", "platform"})
        },
        indexes = {
                @Index(name = "idx_post_share_platform", columnList = "platform")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class PostShareModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostModel post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SharePlatformEnum platform;

    @Column(length = 255)
    private String shareUrl;
}
