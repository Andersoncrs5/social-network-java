package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postCategory.dtos.CreatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.PostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.UpdatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.models.PostCategoriesModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { PostMapper.class, CategoryMapper.class }
)
public interface PostCategoriesMapper {

    PostCategoriesDTO toDTO(@NotNull PostCategoriesModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostCategoriesModel toModel(@NotNull CreatePostCategoriesDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void merge(UpdatePostCategoriesDTO dto, @MappingTarget PostCategoriesModel category);

}
