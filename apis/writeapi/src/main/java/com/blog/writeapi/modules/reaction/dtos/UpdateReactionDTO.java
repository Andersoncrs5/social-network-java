package com.blog.writeapi.modules.reaction.dtos;

import com.blog.writeapi.utils.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.annotations.validations.reaction.existsReactionEmojiUnicode.ExistsReactionEmojiUnicode;
import com.blog.writeapi.utils.annotations.validations.reaction.existsReactionName.ExistsReactionName;
import jakarta.validation.constraints.Size;

public record UpdateReactionDTO(

        @ExistsReactionName
        String name,

        @Size(max = 1200)
        String emojiUrl,

        @ExistsReactionEmojiUnicode
        String emojiUnicode,

        Long displayOrder,
        Boolean active,
        Boolean visible,
        ReactionTypeEnum type
) {
}
