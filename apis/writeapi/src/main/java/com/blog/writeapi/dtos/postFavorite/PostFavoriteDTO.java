package com.blog.writeapi.dtos.postFavorite;

import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.user.UserDTO;

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
