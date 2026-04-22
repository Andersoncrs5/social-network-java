package com.blog.writeapi.modules.postReaction.service.docs;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

import java.util.Optional;

public interface IPostReactionService {
    ResultToggle<PostReactionModel> toggle(
            @IsModelInitialized PostModel post,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    );
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
