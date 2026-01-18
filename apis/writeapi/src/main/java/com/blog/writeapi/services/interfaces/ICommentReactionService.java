package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;

import java.util.Optional;

public interface ICommentReactionService {
    Optional<CommentReactionModel> findByUserAndComment(UserModel user, CommentModel comment);
    CommentReactionModel create(CommentModel comment, ReactionModel reaction, UserModel user);
    void delete(CommentReactionModel model);
    CommentReactionModel updateSimple(CommentReactionModel model);
}
