package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postFavorite.dtos.PostFavoriteDTO;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class, PostMapper.class }
)
public interface PostFavoriteMapper {

    PostFavoriteDTO toDTO(@NotNull PostFavoriteModel model);
    PostFavoriteModel toModel(@NotNull PostFavoriteDTO dto);

}
