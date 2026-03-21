package com.blog.writeapi.modules.category.service.providers;


import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.category.dtos.CreateCategoryDTO;
import com.blog.writeapi.modules.category.dtos.UpdateCategoryDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.category.repository.CategoryRepository;
import com.blog.writeapi.modules.category.service.docs.ICategoryService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slug.Slug;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CategoryMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository repository;
    private final Snowflake generator;
    private final CategoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryModel> getById(@IsId Long id) { return this.repository.findById(id); }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "category", key = "'category:' + #id")
    public CategoryModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() -> new ModelNotFoundException("Category not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) { return repository.existsById(id); }

    @Override
    @CacheEvict(value = "category", key = "'category:' + #category.id")
    public void delete(@IsModelInitialized CategoryModel category) { this.repository.delete(category); }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryModel> getByName(String name) { return this.repository.findByNameIgnoreCase(name); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) { return repository.existsByNameIgnoreCase(name); }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryModel> getBySlug(@Slug String slug) { return this.repository.findBySlugIgnoreCase(slug); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsBySlug(@Slug String slug) { return repository.existsBySlugIgnoreCase(slug); }

    /**
     * @deprecated Use {@link #create(CreateCategoryDTO, CategoryModel)} instead.
     * This method will be removed in the upcoming version due to misconfiguration.
     */
    @Override
    @Deprecated()
    public CategoryModel create(CreateCategoryDTO dto) {
        CategoryModel category = this.mapper.toModel(dto);
        category.setId(generator.nextId());

        return this.repository.save(category);
    }

    @Override
    @CachePut(value = "category", key = "'category:' + #id")
    public CategoryModel create(CreateCategoryDTO dto, CategoryModel categoryModel) {
        CategoryModel category = this.mapper.toModel(dto);
        category.setId(generator.nextId());
        category.setParent(categoryModel);

        return this.repository.save(category);
    }

    @Override
    @Retry(name = "update-retry")
    @CachePut(value = "category", key = "'category:' + #id")
    public CategoryModel update(UpdateCategoryDTO dto, @IsModelInitialized CategoryModel category) {
        mapper.merge(dto, category);

        return this.repository.save(category);
    }

    @Override
    @Retry(name = "update-retry")
    @CachePut(value = "category", key = "'category:' + #id")
    public CategoryModel update(UpdateCategoryDTO dto, @IsModelInitialized CategoryModel category, CategoryModel parent) {
        mapper.merge(dto, category);

        if (Boolean.TRUE.equals(dto.isRoot())) {
            category.setParent(null);
        } else if (parent != null) {
            category.setParent(parent);
        }

        return this.repository.save(category);
    }

}
