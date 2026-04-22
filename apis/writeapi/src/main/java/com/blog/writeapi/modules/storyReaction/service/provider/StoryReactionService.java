package com.blog.writeapi.modules.storyReaction.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyReaction.gateway.StoryReactionModuleGateway;
import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.modules.storyReaction.repository.StoryReactionRepository;
import com.blog.writeapi.modules.storyReaction.service.interfaces.IStoryReactionService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StoryReactionService implements IStoryReactionService {

    private final StoryReactionRepository repository;
    private final StoryReactionModuleGateway gateway;
    private final Snowflake snowflake;

    public void delete(@IsModelInitialized StoryReactionModel model) {
        this.repository.delete(model);
    }

    public StoryReactionModel create(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    ) {
        ReactionModel reaction = this.gateway.findReactionById(reactionId);
        StoryModel story = this.gateway.findStoryById(storyId);
        UserModel user = this.gateway.findUserById(userId);

        if (!story.getUser().getId().equals(userId)) {
            if (this.gateway.isBlocked(userId, story.getUser().getId())) {
                throw new BusinessRuleException("You cannot react to a story from a blocked user.");
            }
        }

        StoryReactionModel model = new StoryReactionModel().toBuilder()
                .id(snowflake.nextId())
                .story(story)
                .reaction(reaction)
                .user(user)
                .build();

        try {
            return repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_story_user_reaction")) {
                throw new UniqueConstraintViolationException(
                        "This story already has this type reacted."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating PostReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Transactional
    public ResultToggle<StoryReactionModel> react(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    ) {
        Optional<StoryReactionModel> optional = this.repository
                .findByUserIdAndStoryIdAndReactionId(userId, storyId, reactionId);

        if (optional.isPresent() && Objects.equals(optional.get().getReaction().getId(), reactionId)) {
            this.delete(optional.get());

            return ResultToggle.removed();
        }

        if (optional.isPresent() && !Objects.equals(optional.get().getReaction().getId(), reactionId)) {
            ReactionModel reaction = this.gateway.findReactionById(reactionId);
            StoryReactionModel current = optional.get();

            current.setReaction(reaction);

            StoryReactionModel saved = this.repository.save(current);

            return ResultToggle.updated(saved);
        }

        StoryReactionModel model = this.create(userId, storyId, reactionId);

        return ResultToggle.added(model);
    }

    @Transactional
    public ResultToggle<StoryReactionModel> react1(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    ) {
        Optional<StoryReactionModel> optional = this.repository
                .findByUserIdAndStoryId(userId, storyId);

        if (optional.isPresent()) {
            StoryReactionModel current = optional.get();

            if (Objects.equals(current.getReaction().getId(), reactionId)) {
                this.delete(current);
                return ResultToggle.removed();
            }

            ReactionModel newReaction = this.gateway.findReactionById(reactionId);
            current.setReaction(newReaction);

            StoryReactionModel saved = this.repository.save(current);
            return ResultToggle.updated(saved);
        }

        StoryReactionModel model = this.create(userId, storyId, reactionId);
        return ResultToggle.added(model);
    }

}
