package com.blog.writeapi.modules.postVote.service.docs;

import com.blog.writeapi.modules.postVote.dtos.TogglePostVoteDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPostVoteService {
    Optional<PostVoteModel> findByUserAndPost(@IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
    void delete(@IsModelInitialized PostVoteModel vote);
    PostVoteModel create(TogglePostVoteDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
    PostVoteModel updateSimple(@IsModelInitialized PostVoteModel vote);
    ResultToggle<PostVoteModel> toggle(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            @NotNull TogglePostVoteDTO dto
    );
}
