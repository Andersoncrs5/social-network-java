package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.commentFavorite.dtos.CommentFavoriteDTO;
import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class, CommentMapper.class }
)
public interface CommentFavoriteMapper {

    CommentFavoriteDTO toDTO(@NotNull CommentFavoriteModel model);

}
