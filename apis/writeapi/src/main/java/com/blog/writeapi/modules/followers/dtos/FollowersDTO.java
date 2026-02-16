package com.blog.writeapi.modules.followers.dtos;

import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record FollowersDTO(
        Long id,
        Boolean isMuted,
        Boolean notifyPosts,
        Boolean notifyComments,
        UserDTO follower,
        UserDTO following,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
