package com.blog.writeapi.dtos.postCategories;

import com.blog.writeapi.utils.annotations.valid.PostCategories.uniquePrimairyInPost.UniquePrimaryCategoryInPost;
import com.blog.writeapi.utils.annotations.valid.category.existsByCategoryId.ExistsByCategoryId;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.post.existsPostById.ExistsPostById;

@UniquePrimaryCategoryInPost
public record CreatePostCategoriesDTO(

        @IsId
        @ExistsPostById
        Long postId,

        @IsId
        @ExistsByCategoryId
        Long categoryId,

        Integer displayOrder,
        boolean primary,
        boolean active
) {
}
