package com.blog.writeapi.modules.commentFavorite.models;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "comments_favorites",
        indexes = {
                @Index(name = "idx_comments_favorites_comment_id", columnList = "comment_id"),
                @Index(name = "idx_comments_favorites_user_id", columnList = "user_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comments_favorites", columnNames = {"user_id", "comment_id"})
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CommentFavoriteModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "comment_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private CommentModel comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel user;
}
