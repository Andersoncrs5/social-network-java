package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.comment.CreateCommentDTO;
import com.blog.writeapi.dtos.comment.UpdateCommentDTO;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentRepository;
import com.blog.writeapi.services.interfaces.ICommentService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
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
    public void delete(@NotNull CommentModel comment) {
        this.repository.delete(comment);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public CommentModel create(CreateCommentDTO dto, PostModel post, UserModel user) {
        CommentModel model = this.mapper.toModel(dto);

        model.setAuthor(user);
        model.setPost(post);
        model.setId(this.generator.nextId());

        return repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public CommentModel update(UpdateCommentDTO dto, CommentModel comment) {
        this.mapper.merge(dto, comment);

        return this.repository.save(comment);
    }

}
