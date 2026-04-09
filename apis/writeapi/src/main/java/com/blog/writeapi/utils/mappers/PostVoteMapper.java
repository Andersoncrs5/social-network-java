package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postVote.dtos.PostVoteDTO;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        uses = { UserMapper.class, PostMapper.class },
        componentModel = "spring"
)
public interface PostVoteMapper {

    PostVoteDTO toDTO(@NotNull PostVoteModel model);

}
