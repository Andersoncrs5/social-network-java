package com.blog.writeapi.modules.tag.models;

import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tag_name", columnList = "name", unique = true),
        @Index(name = "idx_tag_slug", columnList = "slug", unique = true),
        @Index(name = "idx_tag_active", columnList = "is_active"),
        @Index(name = "idx_tag_trending", columnList = "posts_count, last_used_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true) @ToString
@EntityListeners(AuditingEntityListener.class)
public class TagModel extends BaseEntity {

    @Column(nullable = false, length = 70)
    private String name;

    @Column(nullable = false, length = 80)
    private String slug;

    @Column(length = 200)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    @Builder.Default
    @Column(name = "posts_count", columnDefinition = "BIGINT UNSIGNED")
    private Long postsCount = 0L;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostTagModel> tags;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserTagPreferenceModel> userTagPreferences;
}