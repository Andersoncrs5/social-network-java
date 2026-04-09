package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.commentVote.dtos.CommentVoteDTO;
import com.blog.writeapi.modules.commentVote.models.CommentVoteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = {CommentMapper.class, UserMapper.class}
)
public interface CommentVoteMapper {
    CommentVoteDTO toDTO(@NotNull CommentVoteModel model);
}
