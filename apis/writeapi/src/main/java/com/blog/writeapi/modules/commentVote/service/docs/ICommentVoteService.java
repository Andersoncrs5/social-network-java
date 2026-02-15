package com.blog.writeapi.modules.commentVote.service.docs;

import com.blog.writeapi.modules.commentVote.dtos.ToggleCommentVoteDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentVote.models.CommentVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentVoteService {
    Optional<CommentVoteModel> findByUserAndComment(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    );
    void delete(
            @IsModelInitialized CommentVoteModel vote
    );
    CommentVoteModel create(
            ToggleCommentVoteDTO dto,
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    CommentVoteModel updateSimple(@IsModelInitialized CommentVoteModel vote);
}
