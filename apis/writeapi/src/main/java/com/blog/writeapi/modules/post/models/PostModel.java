package com.blog.writeapi.modules.post.models;

import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.modules.postAttachment.models.PostAttachmentModel;
import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.postCategory.models.PostCategoriesModel;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_slug", columnList = "slug", unique = true),
        @Index(name = "idx_post_status", columnList = "status"),
        @Index(name = "idx_post_author", columnList = "author_id"),
        @Index(name = "idx_post_parent", columnList = "parent_id"),
        @Index(name = "idx_post_is_featured", columnList = "is_featured")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostModel extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatusEnum status;

    @Column
    private Integer readingTime;

    @Column
    private Double rankingScore = 0.0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserModel author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", columnDefinition = "BIGINT UNSIGNED")
    private PostModel parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostModel> children = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostCategoriesModel> categories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostTagModel> tags = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentModel> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostFavoriteModel> favoritedBy = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVoteModel> votes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReactionModel> reactions = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReportModel> reports = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostViewModel> viewList = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachmentModel> attachments = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PinnedPostModel> pinnedList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostReadingListModel> savedByUsers = new ArrayList<>();

}