package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CategoryModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<@NonNull CategoryModel, @NonNull Long> {
}
