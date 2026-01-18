package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentReactionService {
    Optional<CommentReactionModel> findByUserAndComment(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    );
    CommentReactionModel create(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    );
    void delete(@IsModelInitialized CommentReactionModel model);
    CommentReactionModel updateSimple(@IsModelInitialized CommentReactionModel model);
}
