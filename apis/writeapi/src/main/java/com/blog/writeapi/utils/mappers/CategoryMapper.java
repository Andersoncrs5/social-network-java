package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.category.dtos.CreateCategoryDTO;
import com.blog.writeapi.modules.category.dtos.UpdateCategoryDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring"
)
public interface CategoryMapper {

    CategoryModel toModel(CategoryDTO dto);
    CategoryDTO toDTO(@IsModelInitialized CategoryModel category);

    @Mapping(target = "parent", ignore = true)
    CategoryModel toModel(CreateCategoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateCategoryDTO dto, @MappingTarget CategoryModel category);

}
