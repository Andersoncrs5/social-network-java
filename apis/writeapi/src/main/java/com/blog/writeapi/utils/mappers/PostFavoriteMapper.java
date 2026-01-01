package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.postFavorite.PostFavoriteDTO;
import com.blog.writeapi.models.PostFavoriteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PostFavoriteMapper {

    PostFavoriteDTO toDTO(@NotNull PostFavoriteModel model);
    PostFavoriteModel toModel(@NotNull PostFavoriteDTO dto);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

}
