package com.blog.writeapi.modules.userView.model;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "user_views",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_view", columnNames = {"user_viewed_id", "viewer_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserViewModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id", nullable = false)
    private UserModel viewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_viewed_id", nullable = false)
    private UserModel viewed;

    @Builder.Default
    private long amount = 1L;
}