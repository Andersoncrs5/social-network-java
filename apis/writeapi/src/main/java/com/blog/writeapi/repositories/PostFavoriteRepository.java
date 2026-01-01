package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostFavoriteModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFavoriteRepository extends JpaRepository<@NonNull PostFavoriteModel, @NonNull Long > {
}
