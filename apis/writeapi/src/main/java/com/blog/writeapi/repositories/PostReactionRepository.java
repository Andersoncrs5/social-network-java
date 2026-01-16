package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.UserModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<@NonNull PostReactionModel, @NonNull Long> {
    Optional<PostReactionModel> findByPostAndUser(PostModel post, UserModel user);

    Boolean existsByPostAndUser(PostModel post, UserModel user);
}
