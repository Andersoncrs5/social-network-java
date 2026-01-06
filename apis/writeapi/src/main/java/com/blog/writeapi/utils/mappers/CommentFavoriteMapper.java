package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.commentFavorite.CommentFavoriteDTO;
import com.blog.writeapi.models.CommentFavoriteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface CommentFavoriteMapper {

    CommentFavoriteDTO toDTO(@NotNull CommentFavoriteModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

}
