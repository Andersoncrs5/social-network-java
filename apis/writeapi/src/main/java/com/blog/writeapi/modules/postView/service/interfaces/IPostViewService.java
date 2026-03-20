package com.blog.writeapi.modules.postView.service.interfaces;

import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.time.LocalDate;

public interface IPostViewService {
    boolean existsByUserAndPost(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post
    );
    boolean existsByUserAndPostAndViewDate(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            LocalDate viewedAtDate
    );
    void delete(@IsModelInitialized PostViewModel view);
    PostViewModel create(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            ClientMetadataDTO metadata,
            LocalDate today
    );
    void recordView(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user,
            ClientMetadataDTO metadata
    );
}
