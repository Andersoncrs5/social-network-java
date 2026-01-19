package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.UpdatePostDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.services.interfaces.IPostService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository repository;
    private final Snowflake generator;
    private final PostMapper mapper;

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
    }

    @Override
    @Retry(name = "create-retry")
    @Transactional
    public PostModel create(@NotNull CreatePostDTO dto, @IsModelInitialized UserModel user) {
        PostModel post = this.mapper.toModel(dto);

        post.setId(generator.nextId());
        post.setAuthor(user);
        post.setStatus(PostStatusEnum.PUBLISHED);

        return this.repository.save(post);
    }

    @Override
    @Retry(name = "update-retry")
    @Transactional
    public PostModel update(@NotNull UpdatePostDTO dto, @IsModelInitialized PostModel post) {
        this.mapper.merge(dto, post);

        return repository.save(post);
    }

}
