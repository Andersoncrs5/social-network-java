package com.blog.writeapi.models;

import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(
        name = "reactions",
        indexes = {
                @Index(name = "idx_reactions_name", columnList = "name"),
                @Index(name = "idx_reactions_emoji_unicode", columnList = "emoji_unicode"),
                @Index(name = "idx_reactions_active", columnList = "is_active")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReactionModel extends BaseEntity {

    @Column(length = 200, unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReactionTypeEnum type;

    @Column(unique = true, columnDefinition = "TEXT")
    private String emojiUrl;

    @Column(length = 20, name = "emoji_unicode")
    private String emojiUnicode;

    @Column(name = "display_order")
    private Long displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @OneToMany(mappedBy = "reaction")
    private List<PostReactionModel> reactionUsages;

}
