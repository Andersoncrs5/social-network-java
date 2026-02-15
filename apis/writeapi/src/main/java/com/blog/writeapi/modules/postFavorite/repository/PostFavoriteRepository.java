package com.blog.writeapi.modules.postFavorite.repository;

import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostFavoriteRepository extends JpaRepository<@NonNull PostFavoriteModel, @NonNull Long > {
    Boolean existsByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);

    Optional<PostFavoriteModel> findByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user);

}
