package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.repositories.PostCategoriesRepository;
import com.blog.writeapi.services.interfaces.IPostCategoriesService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostCategoriesMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class PostCategoriesService implements IPostCategoriesService {

    private final PostCategoriesRepository repository;
    private final PostCategoriesMapper mapper;
    private final Snowflake generator;

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByPostAndCategory(
            @IsModelInitialized PostModel post,
            @IsModelInitialized CategoryModel category
    ){
        return this.repository.existsByPostAndCategory(post, category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPostIdAndPrimaryTrue(@IsId Long postId) {
        return this.repository.existsByPostIdAndPrimaryTrue(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostCategoriesModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PostCategoriesModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() -> new ModelNotFoundException("Category not found"));
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostCategoriesModel model) {
        this.repository.delete(model);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public PostCategoriesModel create(
            @NotNull CreatePostCategoriesDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized CategoryModel category
    ) {
        PostCategoriesModel model = this.mapper.toModel(dto);

        model.setId(generator.nextId());
        model.setPost(post);
        model.setCategory(category);

        return this.repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    @Deprecated
    public PostCategoriesModel update(
            @NotNull UpdatePostCategoriesDTO dto,
            @IsModelInitialized PostCategoriesModel model
            ) {

        this.mapper.merge(dto, model);

        return this.repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public PostCategoriesModel updatev2(
            @NotNull UpdatePostCategoriesDTO dto,
            @IsModelInitialized PostCategoriesModel model
            ) {
        if (dto.primary() != null && dto.primary() && !model.isPrimary()) {
            repository.findByPrimaryTrueAndPost(model.getPost())
                    .ifPresent(oldPrimary -> {
                        oldPrimary.setPrimary(false);
                        repository.save(oldPrimary);
                    });
        }

        this.mapper.merge(dto, model);

        return this.repository.save(model);
    }

}
