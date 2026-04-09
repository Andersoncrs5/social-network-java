package com.blog.writeapi.modules.postReadingList.model;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "post_reading_list",
        indexes = {
                @Index(name = "idx_post_reading_list_post_id", columnList = "post_id"),
                @Index(name = "idx_post_reading_list_user_id", columnList = "user_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_user_reading_list", columnNames = {"post_id", "user_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class PostReadingListModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private PostModel post;

}
