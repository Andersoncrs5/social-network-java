package com.blog.writeapi.utils.res.swagger.postReaction;

import com.blog.writeapi.modules.postReaction.dtos.PostReactionDTO;
import org.springframework.http.ResponseEntity;

public record ResponsePostReactionDTO(
        ResponseEntity<PostReactionDTO> response
) {
}
