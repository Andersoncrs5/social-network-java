package com.blog.writeapi.utils.res.swagger.postReaction;

import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import org.springframework.http.ResponseEntity;

public record ResponsePostReactionDTO(
        ResponseEntity<PostReactionDTO> response
) {
}
