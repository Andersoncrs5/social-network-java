package com.blog.writeapi.dtos.post;

import com.blog.writeapi.utils.annotations.valid.global.slug.Slug;
import com.blog.writeapi.utils.annotations.valid.post.uniqueSlugByPost.UniqueSlugByPost;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record UpdatePostDTO(

        @Size(min = 10, max = 200)
        String title,

        @Slug
        @UniqueSlugByPost
        @Size(min = 10, max = 255)
        String slug,

        @Size(min = 50, max = 3000)
        String content,

        @Max(240)
        Integer readingTime
) {
}
