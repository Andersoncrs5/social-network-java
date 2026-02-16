package com.blog.writeapi.modules.followers.models;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "followers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_followers", columnNames = {"follower_id", "following_id"})
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FollowersModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "follower_id", nullable = false)
    private UserModel follower;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "following_id", nullable = false)
    private UserModel following;

    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private Boolean isMuted = false;

    @Column(name = "notify_posts", nullable = false)
    @Builder.Default
    private Boolean notifyPosts = true;

    @Column(name = "notify_comments", nullable = false)
    @Builder.Default
    private Boolean notifyComments = true;
}
