package com.blog.writeapi.modules.postReaction.repository;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<@NonNull PostReactionModel, @NonNull Long> {
    Optional<PostReactionModel> findByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);

    Boolean existsByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);
}
