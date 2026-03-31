package com.blog.writeapi.modules.pinnedPost.model;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(
        name = "pinned_posts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pinned_posts_user_id_and_post_id", columnNames = {"user_id", "post_id"}
                ),
                @UniqueConstraint(
                        name = "uk_pinned_posts_user_id_and_order_index", columnNames = {"user_id", "order_index"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PinnedPostModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostModel post;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
