package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.postFavorite.PostFavoriteDTO;
import com.blog.writeapi.models.PostFavoriteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostFavoriteMapper {

    PostFavoriteDTO toDTO(@NotNull PostFavoriteModel model);
    PostFavoriteModel toModel(@NotNull PostFavoriteDTO dto);

}
