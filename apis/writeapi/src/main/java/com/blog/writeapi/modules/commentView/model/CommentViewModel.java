package com.blog.writeapi.modules.commentView.model;

import com.blog.writeapi.modules.comment.models.CommentModel;
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
        name = "comment_views",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comment_view_daily", columnNames = {
                        "comment_id", "fingerprint", "viewed_at_date"
                })
        },
        indexes = {
                @Index(name = "idx_comment_views_comment_id", columnList = "comment_id"),
                @Index(name = "idx_comment_views_user_id", columnList = "user_id"),
                @Index(name = "idx_comment_views_viewed_at", columnList = "created_at")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentViewModel extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", columnDefinition = "BIGINT UNSIGNED", nullable = false)
    private CommentModel comment;

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
