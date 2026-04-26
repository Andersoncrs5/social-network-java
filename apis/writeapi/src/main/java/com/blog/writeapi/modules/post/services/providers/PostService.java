package com.blog.writeapi.modules.post.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.UpdatePostDTO;
import com.blog.writeapi.modules.post.gateway.PostModuleGateway;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository repository;
    private final Snowflake generator;
    private final PostMapper mapper;
    private final PostModuleGateway gateway;

    @Override
    @Transactional(readOnly = true)
    public Optional<PostModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    public PostModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() -> new ModelNotFoundException("Post not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) {
        return this.repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsBySlug(@SlugConstraint String slug) {
        return this.repository.existsBySlugIgnoreCase(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostModel> getBySlug(@SlugConstraint String slug) {
        return this.repository.findBySlugIgnoreCase(slug);
    }

    @Override
    @Retry(name = "delete-retry")
    @Transactional
    public void delete(@IsModelInitialized PostModel post) {
        this.repository.delete(post);

        if (post.getParent() != null)
            this.gateway.handleMetric(
                    PostMetricEventDTO.create(post.getParent().getId(), PostMetricEnum.PARENT, ActionEnum.RED)
            );
    }

    @Override @Transactional
    @Retry(name = "delete-retry")
    public void deleteAndCount(@IsId Long id) {
        int result = repository.deleteAndCount(id);

        if (Objects.equals(result, 0)) {
            throw new ModelNotFoundException("Post tag not found");
        }

        PostModel post = this.getByIdSimple(id);

        if (post.getParent() != null)
            this.gateway.handleMetric(
                    PostMetricEventDTO.create(post.getParent().getId(), PostMetricEnum.PARENT, ActionEnum.RED)
            );
    }

    @Override
    @Retry(name = "create-retry")
    @Transactional
    public PostModel create(@NotNull CreatePostDTO dto, @IsModelInitialized UserModel user) {
        PostModel post = this.mapper.toModel(dto);

        post.setId(generator.nextId());
        post.setAuthor(user);
        post.setStatus(PostStatusEnum.PUBLISHED);

        if (dto.parentId() != null) {
            PostModel parent = repository.findById(dto.parentId())
                    .orElseThrow(() -> new ModelNotFoundException("Parent post not found"));

            post.setParent(parent);

        }

        PostModel postSaved = this.repository.save(post);
        this.gateway.handleMetric(PostMetricEventDTO.create(dto.parentId(), PostMetricEnum.PARENT, ActionEnum.SUM));
        return postSaved;
    }

    @Override
    @Retry(name = "update-retry")
    @Transactional
    public PostModel update(@NotNull UpdatePostDTO dto, @IsModelInitialized PostModel post) {
        this.mapper.merge(dto, post);

        return repository.save(post);
    }

}
