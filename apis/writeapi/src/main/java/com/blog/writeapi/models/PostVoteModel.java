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
        name = "post_votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_vote", columnNames = {"post_id", "user_id"})
        }, indexes = {
                @Index(name = "idx_post_vote_user_id", columnList = "user_id"),
                @Index(name = "idx_post_vote_post_id", columnList = "post_id"),

        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostVoteModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private PostModel post;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    private VoteTypeEnum type;
}
