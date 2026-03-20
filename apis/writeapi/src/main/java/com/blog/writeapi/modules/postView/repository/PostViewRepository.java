package com.blog.writeapi.modules.postView.repository;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PostViewRepository extends JpaRepository<PostViewModel, Long> {

    boolean existsByUserAndPost(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post
    );

    boolean existsByUserAndPostAndViewedAtDate(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            LocalDate today
    );

}