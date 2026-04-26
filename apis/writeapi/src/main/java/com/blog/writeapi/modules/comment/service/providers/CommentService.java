package com.blog.writeapi.modules.comment.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.modules.comment.gateway.CommentModuleGateway;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentMapper;
import com.blog.writeapi.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final Snowflake generator;
    private final CommentModuleGateway gateway;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Comment not found")
        );
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized CommentModel comment) {
        this.repository.delete(comment);

        this.gateway.handleMetric(
                PostMetricEventDTO.create(comment.getPost().getId(), PostMetricEnum.COMMENT, ActionEnum.RED)
        );
    }

    @Override
    @Retry(name = "delete-retry")
    @Transactional
    public Result<Void> deleteByID(@IsId Long id) {
        int result = this.repository.deleteByID(id);

        if (result == 0)
            return Result.notFound("Comment not found");

        var comment = this.getByIdSimple(id);

        this.gateway.handleMetric(
                PostMetricEventDTO.create(comment.getPost().getId(), PostMetricEnum.COMMENT, ActionEnum.RED)
        );

        if (comment.getParent() != null)
            this.gateway.handleMetricComment(
                CommentMetricEventDTO.create(
                    comment.getParent().getId(),
                    CommentMetricEnum.PARENT,
                    ActionEnum.RED
                )
            );

        return Result.success();
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public CommentModel create(
            CreateCommentDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        CommentModel model = this.mapper.toModel(dto);

        model.setAuthor(user);
        model.setPost(post);
        model.setId(this.generator.nextId());

        return repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public CommentModel create(
            CreateCommentDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user,
            CommentModel comment
    ) {
        CommentModel model = this.mapper.toModel(dto);

        model.setAuthor(user);
        model.setPost(post);
        model.setParent(comment);
        model.setStatus(CommentStatusEnum.APPROVED);

        model.setId(this.generator.nextId());

        CommentModel save = repository.save(model);
        this.gateway.handleMetric(
                PostMetricEventDTO.create(post.getId(), PostMetricEnum.COMMENT, ActionEnum.SUM)
        );

        if (model.getParent() != null)
            this.gateway.handleMetricComment(
                    CommentMetricEventDTO.create(save.getParent().getId(), CommentMetricEnum.PARENT, ActionEnum.SUM)
            );

        return save;
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public CommentModel update(
            UpdateCommentDTO dto,
            @IsModelInitialized CommentModel comment
    ) {
        this.mapper.merge(dto, comment);

        return this.repository.save(comment);
    }

}
