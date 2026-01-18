package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IPostVoteService {
    Optional<PostVoteModel> findByUserAndPost(@IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
    void delete(@IsModelInitialized PostVoteModel vote);
    PostVoteModel create(TogglePostVoteDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
    PostVoteModel updateSimple(@IsModelInitialized PostVoteModel vote);
}
