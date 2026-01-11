package com.blog.writeapi.repositories;

import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<@NonNull ReactionModel, @NonNull Long> {
    Boolean existsByName(@NotBlank String name);
    Boolean existsByEmojiUnicode(@NotBlank String uni);

}
