package com.blog.writeapi.modules.pinnedPost.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePinnedPostDTO(
        @IsId Long postId,
        @Positive @NotNull int orderIndex
) {
}
