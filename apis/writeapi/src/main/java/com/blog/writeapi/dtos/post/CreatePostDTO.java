package com.blog.writeapi.dtos.post;

import com.blog.writeapi.models.enums.Post.PostStatusEnum;

public record CreatePostDTO(
        String title,
        String slug,
        String content,
        PostStatusEnum status,
        Integer readingTime,
        Double rankingScore,
        Boolean isFeatured
) {
}
