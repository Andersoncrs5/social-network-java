package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostCategoriesModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

interface PostCategoriesRepository extends JpaRepository<@NonNull PostCategoriesModel, @NonNull Long> {
}
