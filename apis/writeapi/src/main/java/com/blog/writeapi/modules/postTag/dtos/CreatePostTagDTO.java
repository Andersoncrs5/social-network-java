package com.blog.writeapi.modules.postTag.dtos;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.post.existsPostById.ExistsPostById;
import com.blog.writeapi.utils.annotations.validations.postTag.uniqueTagInPost.UniqueTagInPost;
import com.blog.writeapi.utils.annotations.validations.tag.existsTagById.ExistsTagById;

@UniqueTagInPost
public record CreatePostTagDTO(

        @IsId
        @ExistsPostById
        Long postId,

        @IsId
        @ExistsTagById
        Long tagId,

        Boolean active,
        Boolean visible
) {
}
