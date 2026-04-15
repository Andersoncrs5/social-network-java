package com.blog.writeapi.modules.storyReaction.model;

import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "story_reactions",
        indexes = {
                @Index(name = "idx_story_reactions_story", columnList = "story_id"),
                @Index(name = "idx_story_reactions_user", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_story_user_reaction", columnNames = {"story_id", "user_id", "reaction_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class StoryReactionModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false, updatable = false)
    private StoryModel story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionModel reaction;

}
