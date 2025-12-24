package com.blog.writeapi.models;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "post_categories",
        indexes = {
                @Index(name = "idx_post_category_post_id", columnList = "post_id"),
                @Index(name = "idx_post_category_category_id", columnList = "category_id"),
                @Index(name = "idx_post_category_display_order", columnList = "display_order"),
                @Index(name = "idx_post_category_is_primary", columnList = "is_primary")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_category", columnNames = {"post_id", "category_id"})
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoriesModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private PostModel post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_primary")
    private boolean primary;

    @Column(name = "is_active")
    private boolean active;

}