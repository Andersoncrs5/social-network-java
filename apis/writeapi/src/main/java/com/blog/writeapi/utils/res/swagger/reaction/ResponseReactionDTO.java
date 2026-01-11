package com.blog.writeapi.utils.res.swagger.reaction;

import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseReactionDTO(
        ResponseHttp<ReactionDTO> dto
) {
}
