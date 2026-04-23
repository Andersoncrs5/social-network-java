package com.blog.writeapi.modules.postTag.models;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "post_tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_tag", columnNames = {"post_id", "tag_id"})
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostTagModel extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "post_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private PostModel post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tag_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private TagModel tag;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;
}