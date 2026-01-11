package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.UpdateReactionDTO;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import jakarta.validation.constraints.NotBlank;

public interface IReactionService {
    ReactionModel getByIdSimple(@IsId Long id);
    Boolean existsByName(@NotBlank String name);
    Boolean existsById(@IsId Long id);
    void delete(ReactionModel reaction);
    ReactionModel create(CreateReactionDTO dto);
    ReactionModel update(UpdateReactionDTO dto, ReactionModel reaction);
    Boolean existsByEmojiUnicode(@NotBlank String uni);
}
