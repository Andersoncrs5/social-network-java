package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.models.PostReactionModel;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {PostMapper.class, UserMapper.class, ReactionMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostReactionMapper {

    @Mapping(target = "updateAt", source = "updatedAt")
    PostReactionDTO toDTO(@NotNull PostReactionModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}