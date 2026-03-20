package com.blog.writeapi.modules.postView.model;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "post_views",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_view_daily", columnNames = {
                        "post_id", "fingerprint", "viewed_at_date"
                })
        },
        indexes = {
                @Index(name = "idx_post_views_post_id", columnList = "post_id"),
                @Index(name = "idx_post_views_user_id", columnList = "user_id"),
                @Index(name = "idx_post_views_viewed_at", columnList = "created_at")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostViewModel extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id", nullable = false, updatable = false)
        private PostModel post;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", columnDefinition = "BIGINT UNSIGNED", nullable = false)
        private UserModel user;

        @Column(name = "ip_address", length = 45, nullable = false)
        private String ipAddress;

        @Column(name = "user_agent", length = 500)
        private String userAgent;

        @Column(name = "fingerprint")
        private String fingerprint;

        @Column(name = "viewed_at_date", nullable = false)
        private LocalDate viewedAtDate;

        @Column(name = "is_bot", nullable = false)
        private boolean bot = false;
}