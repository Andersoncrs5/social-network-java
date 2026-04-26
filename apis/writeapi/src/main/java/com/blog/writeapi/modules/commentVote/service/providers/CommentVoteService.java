package com.blog.writeapi.modules.commentVote.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.commentVote.dtos.ToggleCommentVoteDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentVote.gateway.CommentVoteModuleGateway;
import com.blog.writeapi.modules.commentVote.models.CommentVoteModel;
import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.commentVote.repository.CommentVoteRepository;
import com.blog.writeapi.modules.commentVote.service.docs.ICommentVoteService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentVoteService implements ICommentVoteService {

    private final CommentVoteRepository repository;
    private final Snowflake generator;
    private final CommentVoteModuleGateway gateway;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentVoteModel> findByUserAndComment(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        return this.repository.findByUserAndComment(user, comment);
    }

    @Override
    @Retry(name = "delete-retry")
    public void delete(
            @IsModelInitialized CommentVoteModel vote
    ) {
        repository.delete(vote);
        if (vote.getType().equals(VoteTypeEnum.UPVOTE)) {
            gateway.handleMetricComment(CommentMetricEventDTO.create(
                    vote.getComment().getId(),
                    CommentMetricEnum.UPVOTE,
                    ActionEnum.RED
            ));
        }

        if (vote.getType().equals(VoteTypeEnum.DOWNVOTE)) {
            gateway.handleMetricComment(CommentMetricEventDTO.create(
                    vote.getComment().getId(),
                    CommentMetricEnum.DOWNVOTE,
                    ActionEnum.RED
            ));
        }
    }

    @Override
    @Retry(name = "create-retry")
    public CommentVoteModel create(
            ToggleCommentVoteDTO dto,
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        if (!user.getId().equals(comment.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), comment.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot vote on a comment from a blocked user.");
            }
        }

        CommentVoteModel vote = new CommentVoteModel().toBuilder()
                .id(this.generator.nextId())
                .type(dto.type())
                .user(user)
                .comment(comment)
                .build();

        try {
            CommentVoteModel saved = this.repository.save(vote);

            if (saved.getType().equals(VoteTypeEnum.UPVOTE)) {
                gateway.handleMetricComment(CommentMetricEventDTO.create(
                        saved.getComment().getId(),
                        CommentMetricEnum.UPVOTE,
                        ActionEnum.SUM
                ));
            }

            if (saved.getType().equals(VoteTypeEnum.DOWNVOTE)) {
                gateway.handleMetricComment(CommentMetricEventDTO.create(
                        saved.getComment().getId(),
                        CommentMetricEnum.DOWNVOTE,
                        ActionEnum.SUM
                ));
            }

            return saved;
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_comment_vote")) {
                throw new UniqueConstraintViolationException("You have already voted on this comment.");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating comment vote: ", e);
            throw new InternalServerErrorException("Error processing your vote.");
        }
    }

    @Override
    @Retry(name = "update-retry")
    public CommentVoteModel updateSimple(@IsModelInitialized CommentVoteModel vote) {
        return this.repository.save(vote);
    }

}
