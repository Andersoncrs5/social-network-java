package com.blog.writeapi.utils.res.swagger.reaction;

import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseReactionDTO(
        ResponseHttp<ReactionDTO> dto
) {
}
