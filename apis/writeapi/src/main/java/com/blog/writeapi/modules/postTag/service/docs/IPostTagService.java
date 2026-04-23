package com.blog.writeapi.modules.postTag.service.docs;

import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.UpdatePostTagDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPostTagService {
    PostTagModel getByIdSimple(@IsId Long id);
    Optional<PostTagModel> getById(@IsId Long id);
    void delete(@IsModelInitialized PostTagModel model);
    PostTagModel create(
            @NotNull CreatePostTagDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized TagModel tag,
            @IsId Long userId
    );
    PostTagModel update(
            @NotNull UpdatePostTagDTO dto,
            @IsModelInitialized PostTagModel model
    );
    Boolean existsByPostAndTag(
            @IsId Long postId,
            @IsId Long tagId
    );
    void deleteByID(@IsId Long id);
}
