package com.blog.writeapi.modules.postTag.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.UpdatePostTagDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.postTag.repository.PostTagRepository;
import com.blog.writeapi.modules.postTag.service.docs.IPostTagService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.PostTagMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
            @IsModelInitialized TagModel tag,
            @IsId Long userId
    ) {
        if (!tag.getIsActive()) {
            throw new BusinessRuleException("Tag is inactive", HttpStatus.FORBIDDEN);
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new BusinessRuleException("You are not the author of this post", HttpStatus.FORBIDDEN);
        }

        PostTagModel model = this.mapper.toModel(dto);
        model.setId(this.generator.nextId());
        model.setPost(post);
        model.setTag(tag);

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("This tag has already been added to this post.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Error processing your request.");
        }
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
