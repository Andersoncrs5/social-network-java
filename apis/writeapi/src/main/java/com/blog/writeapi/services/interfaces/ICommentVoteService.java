package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentVoteModel;
import com.blog.writeapi.models.UserModel;

import java.util.Optional;

public interface ICommentVoteService {
    Optional<CommentVoteModel> findByUserAndComment(UserModel user, CommentModel comment);
    void delete(CommentVoteModel vote);
    CommentVoteModel create(ToggleCommentVoteDTO dto, CommentModel comment, UserModel user);
    CommentVoteModel updateSimple(CommentVoteModel vote);
}
