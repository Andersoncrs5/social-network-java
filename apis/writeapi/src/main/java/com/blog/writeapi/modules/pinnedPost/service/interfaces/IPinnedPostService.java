package com.blog.writeapi.modules.pinnedPost.service.interfaces;

import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

public interface IPinnedPostService {
    boolean existsByUserIdAndPostId(
            @IsId Long userId,
            @IsId Long postId
    );
    boolean existsByUserIdAndOrderIndex(
            @IsId Long userId,
            int index
    );
    void delete(@IsModelInitialized PinnedPostModel pinned);
    PinnedPostModel create(
            @IsId Long userId,
            CreatePinnedPostDTO dto
    );
    PinnedPostModel update(
            @IsModelInitialized PinnedPostModel pinned,
            UpdatePinnedPostDTO dto
    );
}
