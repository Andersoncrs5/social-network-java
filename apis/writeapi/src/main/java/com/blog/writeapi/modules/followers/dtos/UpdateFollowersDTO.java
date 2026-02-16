package com.blog.writeapi.modules.followers.dtos;

public record UpdateFollowersDTO(
        Boolean isMuted,
        Boolean notifyPosts,
        Boolean notifyComments
) {
}
