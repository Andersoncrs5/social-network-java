package com.blog.writeapi.dtos.reaction;

import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.annotations.valid.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionEmojiUnicode.UniqueReactionEmojiUnicode;
import com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionName.UniqueReactionName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReactionDTO(

        @UniqueReactionName
        @StringClear
        String name,

        @Size(max = 1200)
        String emojiUrl,

        @UniqueReactionEmojiUnicode
        String emojiUnicode,

        Long displayOrder,

        @NotNull
        Boolean active,

        @NotNull
        Boolean visible,

        @NotNull
        ReactionTypeEnum type
) {
}
