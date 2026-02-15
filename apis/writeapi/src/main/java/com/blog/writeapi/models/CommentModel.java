package com.blog.writeapi.models;

import com.blog.writeapi.models.enums.comment.CommentStatusEnum;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_author", columnList = "author_id"),
                @Index(name = "idx_comment_parent", columnList = "parent_id"),
                @Index(name = "idx_comment_pinned", columnList = "is_pinned"),
                @Index(name = "idx_comment_status", columnList = "status")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentModel extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommentStatusEnum status = CommentStatusEnum.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private PostModel post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private UserModel author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", columnDefinition = "BIGINT UNSIGNED")
    private CommentModel parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CommentModel> replies;

    @Column(name = "is_edited")
    private boolean edited = false;

    @Column(name = "is_pinned")
    private boolean pinned = false;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @JsonIgnore
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentFavoriteModel> favoritedBy = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentVoteModel> votes = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentReactionModel> reactions;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentAttachmentModel> attachments;

}
