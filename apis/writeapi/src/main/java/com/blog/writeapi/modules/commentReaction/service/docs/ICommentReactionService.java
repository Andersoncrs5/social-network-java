package com.blog.writeapi.modules.commentReaction.service.docs;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

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
