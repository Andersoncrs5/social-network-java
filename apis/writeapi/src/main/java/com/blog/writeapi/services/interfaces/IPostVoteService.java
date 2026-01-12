package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;

import java.util.Optional;

public interface IPostVoteService {
    Optional<PostVoteModel> findByUserAndPost(UserModel user, PostModel post);
    void delete(PostVoteModel vote);
    PostVoteModel create(TogglePostVoteDTO dto, UserModel user, PostModel post);
    PostVoteModel updateSimple(PostVoteModel vote);
}
