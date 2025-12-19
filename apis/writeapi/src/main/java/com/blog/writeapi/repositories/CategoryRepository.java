package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CategoryModel;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<@NonNull CategoryModel, @NonNull Long> {
    Boolean existsByName(String name);
    Optional<CategoryModel> findByName(String name);
    Optional<CategoryModel> findBySlug(String slug);
    Boolean existsBySlug(String slug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM CategoryModel t WHERE t.id = :id")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "4000")})
    Optional<CategoryModel> findByIdForUpdate(@Param("id") Long id);
}
