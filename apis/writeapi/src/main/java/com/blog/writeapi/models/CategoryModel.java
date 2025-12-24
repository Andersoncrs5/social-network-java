package com.blog.writeapi.models;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_name", columnList = "name"),
        @Index(name = "idx_category_slug", columnList = "slug"),
        @Index(name = "idx_category_active", columnList = "is_active"),
        @Index(name = "idx_category_parent", columnList = "parent_id")
})
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString(exclude = {"parent", "children"})
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel extends BaseEntity {

    @Column(length = 150, unique = true, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 160, unique = true, nullable = false)
    private String slug;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_visible", nullable = false)
    private Boolean visible = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryModel parent;

    @OneToMany(mappedBy = "parent")
    private List<CategoryModel> children;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCategoriesModel> posts;

}
