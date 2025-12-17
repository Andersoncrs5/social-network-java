package com.blog.writeapi.services.providers;


import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.repositories.CategoryRepository;
import com.blog.writeapi.services.interfaces.ICategoryService;
import com.blog.writeapi.utils.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository repository;
    private final Snowflake generator;
    private final CategoryMapper mapper;

    @Override
    public Optional<CategoryModel> getById(Long id) { return this.repository.findById(id); }

    @Override
    public Boolean existsById(Long id) { return repository.existsById(id); }

    @Override
    public void delete(CategoryModel category) { this.repository.delete(category); }

    @Override
    public Optional<CategoryModel> getByName(String name) { return this.repository.findByName(name); }

    @Override
    public Boolean existsByName(String name) { return repository.existsByName(name); }

    @Override
    public Optional<CategoryModel> getBySlug(String slug) { return this.repository.findBySlug(slug); }

    @Override
    public Boolean existsBySlug(String slug) { return repository.existsBySlug(slug); }

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
    public CategoryModel create(CreateCategoryDTO dto, CategoryModel categoryModel) {
        CategoryModel category = this.mapper.toModel(dto);
        category.setId(generator.nextId());
        category.setParent(categoryModel);

        return this.repository.save(category);
    }

    @Override
    public CategoryModel update(UpdateCategoryDTO dto, CategoryModel category) {
        mapper.merge(dto, category);

        return this.repository.save(category);
    }

}
