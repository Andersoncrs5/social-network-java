package com.blog.writeapi.modules.post.repository;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<@NonNull PostModel, @NonNull Long> {
    Boolean existsBySlugIgnoreCase(@SlugConstraint String slug);

    Optional<PostModel> findBySlugIgnoreCase(@SlugConstraint String slug);
}
