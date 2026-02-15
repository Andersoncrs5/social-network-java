package com.blog.writeapi.modules.reaction.dtos;

import com.blog.writeapi.utils.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.annotations.validations.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.validations.reaction.uniqueReactionEmojiUnicode.UniqueReactionEmojiUnicode;
import com.blog.writeapi.utils.annotations.validations.reaction.uniqueReactionName.UniqueReactionName;
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
