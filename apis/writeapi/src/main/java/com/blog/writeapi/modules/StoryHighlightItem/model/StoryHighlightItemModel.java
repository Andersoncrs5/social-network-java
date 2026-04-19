package com.blog.writeapi.modules.StoryHighlightItem.model;


import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "story_highlight_items",
        indexes = {
                @Index(name = "idx_story_highlight_item_title", columnList = "title"),
                @Index(name = "idx_story_highlight_item_storage_key", columnList = "storage_key", unique = true),
                @Index(name = "idx_story_highlight_item_file_name", columnList = "file_name", unique = true),
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_story_highlight_items_user_story_highlight",
                        columnNames = {"user_id", "story_id", "highlight_id"}
                )
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SuperBuilder(toBuilder = true) @EqualsAndHashCode(callSuper = true)
public class StoryHighlightItemModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_id", nullable = false)
    private StoryHighlightModel highlight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private StoryModel story;

}
