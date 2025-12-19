package com.blog.writeapi.repositories;

import com.blog.writeapi.models.TagModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<@NonNull TagModel, @NonNull Long> {
    Optional<TagModel> findByName(String name);
    Boolean existsByName(String name);

    Optional<TagModel> findBySlug(String slug);
    Boolean existsBySlug(String slug);
}
