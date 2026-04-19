package com.blog.writeapi.modules.storyHighlight.model;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.AttachmentBaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "story_highlights",
        indexes = {
                @Index(name = "uk_story_highlight_title", columnList = "title", unique = true),
                @Index(name = "uk_story_highlight_storage_key", columnList = "storage_key", unique = true),
                @Index(name = "uk_story_highlight_file_name", columnList = "file_name", unique = true),
        }
)
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class StoryHighlightModel extends AttachmentBaseModel {

    @Column(nullable = false, length = 150)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

//    @OneToMany(mappedBy = "highlight", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<StoryHighlightItemModel> items;
}