package com.blog.writeapi.modules.postCategory.dtos;

import com.blog.writeapi.utils.annotations.validations.PostCategories.uniquePrimairyInPost.UniquePrimaryCategoryInPost;
import com.blog.writeapi.utils.annotations.validations.category.existsByCategoryId.ExistsByCategoryId;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.post.existsPostById.ExistsPostById;

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
