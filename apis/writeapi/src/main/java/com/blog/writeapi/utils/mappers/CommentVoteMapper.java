package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.commentVote.CommentVoteDTO;
import com.blog.writeapi.models.CommentVoteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, UserMapper.class})
public interface CommentVoteMapper {

    CommentVoteDTO toDTO(@NotNull CommentVoteModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
