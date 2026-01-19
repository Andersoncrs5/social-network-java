package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<@NonNull PostVoteModel, @NonNull Long> {
    Optional<PostVoteModel> findByUserAndPost(@IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
}
