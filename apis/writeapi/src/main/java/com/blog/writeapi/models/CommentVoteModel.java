package com.blog.writeapi.models;

import com.blog.writeapi.models.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "comment_votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comment_vote", columnNames = {"comment_id", "user_id"})
        },
        indexes = {
            @Index(name = "idx_comment_vote_user_id", columnList = "user_id"),
            @Index(name = "idx_comment_vote_comment_id", columnList = "comment_id"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentVoteModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentModel comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteTypeEnum type;
}
