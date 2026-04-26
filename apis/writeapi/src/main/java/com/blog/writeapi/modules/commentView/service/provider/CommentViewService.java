package com.blog.writeapi.modules.commentView.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentView.gateway.CommentViewModuleGateway;
import com.blog.writeapi.modules.commentView.model.CommentViewModel;
import com.blog.writeapi.modules.commentView.repository.CommentViewRepository;
import com.blog.writeapi.modules.commentView.service.interfaces.ICommentViewService;
import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentViewService implements ICommentViewService {

    private final CommentViewRepository repository;
    private final Snowflake generator;
    private final CommentViewModuleGateway gateway;

    @Override
    public void delete(@IsModelInitialized CommentViewModel view) {
        repository.delete(view);
        gateway.handleMetricComment(CommentMetricEventDTO.create(
                view.getComment().getId(),
                CommentMetricEnum.VIEW,
                ActionEnum.RED
        ));
    }

    @Override
    public boolean existsByUserAndCommentAndViewedAtDate(
            @IsId Long userId,
            @IsId Long commentId,
            LocalDate viewedAtDate
    ) {
        CommentModel comment = this.gateway.getCommentById(commentId);
        UserModel user = this.gateway.getUserById(userId);

        return repository.existsByUserAndCommentAndViewedAtDate(user, comment, viewedAtDate);
    }

    @Override
    public CommentViewModel create(
            @IsId Long userId,
            @IsId Long commentId,
            ClientMetadataDTO metadata,
            LocalDate today
    ) {
        CommentModel comment = this.gateway.getCommentById(commentId);
        UserModel user = this.gateway.getUserById(userId);

        CommentViewModel view = CommentViewModel.builder()
                .id(generator.nextId())
                .comment(comment)
                .user(user)
                .ipAddress(metadata.ipAddress())
                .userAgent(metadata.userAgent())
                .fingerprint(metadata.fingerprint())
                .viewedAtDate(today)
                .bot(metadata.isBot())
                .build();

        try {
            CommentViewModel save = this.repository.save(view);
            gateway.handleMetricComment(CommentMetricEventDTO.create(
                    comment.getId(),
                    CommentMetricEnum.VIEW,
                    ActionEnum.SUM
            ));
            return save;
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_comment_view_daily")) {
                throw new UniqueConstraintViolationException(
                        "User already view this comment"
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Override
    public void recordView(
            @IsId Long userId,
            @IsId Long commentId,
            ClientMetadataDTO metadata
    ) {
        LocalDate today = LocalDate.now();

        CommentModel comment = this.gateway.getCommentById(commentId);

        if (comment.getAuthor().getId().equals(userId)) { return; }

        boolean exists = repository.existsByUserIdAndCommentIdAndViewedAtDate(userId, commentId, today);

        if (!exists) {
            try {
                this.create(userId, commentId, metadata, today);
            } catch (UniqueConstraintViolationException e) {
                log.debug("Double click or race condition ignored for user {} on post {}", userId, comment.getId());
            }
        }
    }

}
