package com.blog.writeapi.modules.commentReaction.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReaction.gateway.CommentReactionModuleGateway;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.commentReaction.repository.CommentReactionRepository;
import com.blog.writeapi.modules.commentReaction.service.docs.ICommentReactionService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import io.github.resilience4j.retry.annotation.Retry;
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
public class CommentReactionService implements ICommentReactionService {

    private final CommentReactionRepository repository;
    private final Snowflake generator;
    private final CommentReactionModuleGateway gateway;

    public ResultToggle<CommentReactionModel> toggle(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    ) {
        Optional<CommentReactionModel> optional = this.repository.findByUserAndComment(user, comment);

        if (optional.isPresent() && Objects.equals(optional.get().getReaction().getId(), reaction.getId())) {
            this.delete(optional.get());
            return ResultToggle.removed();
        }

        if (optional.isPresent()) {
            CommentReactionModel model = optional.get();
            model.setReaction(reaction);
            return ResultToggle.updated(this.updateSimple(model));
        }

        return ResultToggle.added(this.create(comment, reaction, user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentReactionModel> findByUserAndComment(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        return this.repository.findByUserAndComment(user, comment);
    }

    @Override
    @Retry(name = "create-retry")
    public CommentReactionModel create(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    ) {
        if (!user.getId().equals(comment.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), comment.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot react to a comment from a blocked user.");
            }
        }

        CommentReactionModel model = new CommentReactionModel().toBuilder()
                .comment(comment)
                .reaction(reaction)
                .user(user)
                .id(this.generator.nextId())
                .build();

        try {
            CommentReactionModel save = this.repository.save(model);

            this.gateway.handleMetricComment(CommentMetricEventDTO.create(
                    comment.getId(), CommentMetricEnum.REACTION, ActionEnum.SUM
            ));

            return save;
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_comment_user_reaction")) {
                throw new UniqueConstraintViolationException("You have already reacted to this comment.");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating comment reaction: ", e);
            throw new InternalServerErrorException("Error applying reaction to this comment.");
        }
    }

    @Override
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized CommentReactionModel model) {
        this.repository.delete(model);

        this.gateway.handleMetricComment(CommentMetricEventDTO.create(
                model.getComment().getId(), CommentMetricEnum.REACTION, ActionEnum.RED
        ));
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public CommentReactionModel updateSimple(@IsModelInitialized CommentReactionModel model) {
        return repository.save(model);
    }

}
