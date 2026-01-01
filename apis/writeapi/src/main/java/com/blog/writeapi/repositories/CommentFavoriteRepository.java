package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentFavoriteModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentFavoriteRepository extends JpaRepository<@NonNull CommentFavoriteModel, @NonNull Long> {
}
