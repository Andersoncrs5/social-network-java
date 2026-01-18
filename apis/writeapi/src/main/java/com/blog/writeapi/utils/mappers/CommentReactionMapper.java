package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.commentReaction.CommentReactionDTO;
import com.blog.writeapi.models.CommentReactionModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {CommentMapper.class, UserMapper.class, ReactionMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentReactionMapper {

    @Mapping(target = "updateAt", source = "updatedAt")
    CommentReactionDTO toDTO(@NotNull CommentReactionModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
