package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postTag.CreatePostTagDTO;
import com.blog.writeapi.dtos.postTag.UpdatePostTagDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostTagModel;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.repositories.PostTagRepository;
import com.blog.writeapi.services.interfaces.IPostTagService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostTagMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostTagService implements IPostTagService {

    private final PostTagRepository repository;
    private final Snowflake generator;
    private final PostTagMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByPostAndTag(
            @IsId Long postId,
            @IsId Long tagId
    ) {
        return this.repository.existsByPostIdAndTagId(postId, tagId);
    }

    @Override
    @Transactional(readOnly = true)
    public PostTagModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Post tag not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostTagModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostTagModel model) {
        this.repository.delete(model);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public PostTagModel create(
            @NotNull CreatePostTagDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized  TagModel tag
            ) {
        PostTagModel model = this.mapper.toModel(dto);

        model.setId(this.generator.nextId());
        model.setPost(post);
        model.setTag(tag);

        return this.repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public PostTagModel update(
            @NotNull UpdatePostTagDTO dto,
            @IsModelInitialized  PostTagModel model
            ) {
        this.mapper.merge(dto, model);

        return this.repository.save(model);
    }


}
