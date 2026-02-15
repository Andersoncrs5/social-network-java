package com.blog.writeapi.modules.reaction.service.docs;

import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.UpdateReactionDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotBlank;

public interface IReactionService {
    ReactionModel getByIdSimple(@IsId Long id);
    Boolean existsByName(@NotBlank String name);
    Boolean existsById(@IsId Long id);
    void delete(@IsModelInitialized ReactionModel reaction);
    ReactionModel create(CreateReactionDTO dto);
    ReactionModel update(UpdateReactionDTO dto, @IsModelInitialized ReactionModel reaction);
    Boolean existsByEmojiUnicode(@NotBlank String uni);
}
