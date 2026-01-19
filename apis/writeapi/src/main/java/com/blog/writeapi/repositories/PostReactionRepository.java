package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<@NonNull PostReactionModel, @NonNull Long> {
    Optional<PostReactionModel> findByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);

    Boolean existsByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);
}
