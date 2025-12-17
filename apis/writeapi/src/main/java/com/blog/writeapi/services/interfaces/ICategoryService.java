package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;

import java.util.Optional;

public interface ICategoryService {
    Optional<CategoryModel> getById(Long id);
    Boolean existsById(Long id);
    void delete(CategoryModel category);
    CategoryModel create(CreateCategoryDTO dto);
    CategoryModel create(CreateCategoryDTO dto, CategoryModel categoryModel);
    Optional<CategoryModel> getByName(String name);
    Boolean existsByName(String name);
    Optional<CategoryModel> getBySlug(String slug);
    Boolean existsBySlug(String slug);
    CategoryModel update(UpdateCategoryDTO dto, CategoryModel category);
}
