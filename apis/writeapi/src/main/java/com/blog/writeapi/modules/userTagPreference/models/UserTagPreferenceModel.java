package com.blog.writeapi.modules.userTagPreference.models;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "user_tag_preferences",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_tag_preferences", columnNames = {"tag_id", "user_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserTagPreferenceModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = true)
    private UserModel user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", nullable = false, updatable = true)
    private TagModel tag;

    private Double interestScore;

}
