package com.blog.writeapi.dtos.reaction;

import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.annotations.valid.reaction.existsReactionEmojiUnicode.ExistsReactionEmojiUnicode;
import com.blog.writeapi.utils.annotations.valid.reaction.existsReactionName.ExistsReactionName;
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
