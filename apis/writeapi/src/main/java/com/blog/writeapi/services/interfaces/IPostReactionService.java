package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IPostReactionService {
    Optional<PostReactionModel> findByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    Boolean existsByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    void delete(
            @IsModelInitialized PostReactionModel model
    );
    PostReactionModel create(
            @IsModelInitialized PostModel post,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    );
    PostReactionModel updateSimple(@IsModelInitialized PostReactionModel model);
}
