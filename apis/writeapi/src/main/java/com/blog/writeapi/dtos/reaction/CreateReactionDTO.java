package com.blog.writeapi.dtos.reaction;

import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionEmojiUnicode.UniqueReactionEmojiUnicode;
import com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionName.UniqueReactionName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReactionDTO(

        @UniqueReactionName
        String name,

        @Size(max = 1200)
        String emojiUrl,

        @UniqueReactionEmojiUnicode
        String emojiUnicode,

        @NotNull
        Long displayOrder,

        @NotNull
        Boolean active,

        @NotNull
        Boolean visible,

        @NotNull
        ReactionTypeEnum type
) {
}
