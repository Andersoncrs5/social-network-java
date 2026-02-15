package com.blog.writeapi.modules.postFavorite.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record PostFavoriteDTO(
        Long id,
        PostDTO post,
        UserDTO user,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updateAt
) {
}
