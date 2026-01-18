package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

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
