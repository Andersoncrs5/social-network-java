package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.global.slug.Slug;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface ICategoryService {
    Optional<CategoryModel> getById(@IsId Long id);
    CategoryModel getByIdSimple(@IsId Long id);
    Boolean existsById(@IsId Long id);
    void delete(@IsModelInitialized CategoryModel category);
    @Deprecated()
    CategoryModel create(CreateCategoryDTO dto);
    CategoryModel create(CreateCategoryDTO dto, CategoryModel categoryModel);
    Optional<CategoryModel> getByName(String name);
    Boolean existsByName(String name);
    Optional<CategoryModel> getBySlug(@Slug String slug);
    Boolean existsBySlug(@Slug String slug);
    CategoryModel update(UpdateCategoryDTO dto, @IsModelInitialized CategoryModel category);
    CategoryModel update(UpdateCategoryDTO dto, @IsModelInitialized CategoryModel category, CategoryModel parent);
}
