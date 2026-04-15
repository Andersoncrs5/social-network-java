package com.blog.writeapi.modules.storyReaction.repository;

import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryReactionRepository extends JpaRepository<StoryReactionModel, Long> {
    Optional<StoryReactionModel> findByUserIdAndStoryIdAndReactionId(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    );

    Optional<StoryReactionModel> findByUserIdAndStoryId(@IsId Long userId, @IsId Long storyId);
}
