package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostReactionModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReactionRepository extends JpaRepository<@NonNull PostReactionModel, @NonNull Long> {
}
