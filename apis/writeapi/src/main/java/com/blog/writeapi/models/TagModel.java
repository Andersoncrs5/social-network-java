package com.blog.writeapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tag_name", columnList = "name"),
        @Index(name = "idx_tag_slug", columnList = "slug"),
        @Index(name = "idx_tag_active", columnList = "is_active"),
        @Index(name = "idx_tag_trending", columnList = "posts_count, last_used_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class TagModel {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 70)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(length = 200)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    @Column(name = "posts_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long postsCount = 0L;

    @Version
    private Long version;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}