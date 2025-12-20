package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryModel toModel(CategoryDTO dto);
    CategoryDTO toDTO(CategoryModel category);

    @Mapping(target = "parent", ignore = true)
    CategoryModel toModel(CreateCategoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateCategoryDTO dto, @MappingTarget CategoryModel category);
}
