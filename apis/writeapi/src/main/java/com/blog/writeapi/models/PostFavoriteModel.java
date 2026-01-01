package com.blog.writeapi.models;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "posts_favorites",
        indexes = {
                @Index(name = "idx_posts_favorites_post_id", columnList = "post_id"),
                @Index(name = "idx_posts_favorites_user_id", columnList = "user_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_posts_favorites", columnNames = {"user_id", "post_id"})
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PostFavoriteModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "post_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private PostModel post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel user;

}
