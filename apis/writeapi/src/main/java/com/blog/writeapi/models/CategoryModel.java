package com.blog.writeapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

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

    @Version
    private Long version;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "parent")
    private List<CategoryModel> children;

}
