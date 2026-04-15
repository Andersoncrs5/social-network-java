package com.blog.writeapi.modules.storyView.model;

import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "story_views", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"story_id", "user_id"}, name = "uk_story_views_user_story")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class StoryViewModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private StoryModel story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

}