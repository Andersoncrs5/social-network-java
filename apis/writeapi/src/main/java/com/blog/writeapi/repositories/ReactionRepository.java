package com.blog.writeapi.repositories;

import com.blog.writeapi.models.ReactionModel;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<@NonNull ReactionModel, @NonNull Long> {
    Boolean existsByNameIgnoreCase(@NotBlank String name);
    Boolean existsByEmojiUnicodeIgnoreCase(@NotBlank String uni);

}
