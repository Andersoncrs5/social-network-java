package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;

import java.util.Optional;

public interface IPostReactionService {
    Optional<PostReactionModel> findByPostAndUser(PostModel post, UserModel user);
    Boolean existsByPostAndUser(PostModel post, UserModel user);
    void delete(PostReactionModel model);
    PostReactionModel create(PostModel post, ReactionModel reaction, UserModel user);
    PostReactionModel updateSimple(PostReactionModel model);
}
