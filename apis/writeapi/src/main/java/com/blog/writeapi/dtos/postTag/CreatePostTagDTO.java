package com.blog.writeapi.dtos.postTag;

public record CreatePostTagDTO(
        Long postId,
        Long tagId,
        Boolean active,
        Boolean visible
) {
}
