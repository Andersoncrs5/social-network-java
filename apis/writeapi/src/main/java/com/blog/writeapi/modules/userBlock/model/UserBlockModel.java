package com.blog.writeapi.modules.userBlock.model;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "user_blocks",
        indexes = {
            @Index(name = "idx_blocker_blocked", columnList = "blocker_id, blocked_id", unique = true),
            @Index(name = "idx_blocked_id", columnList = "blocked_id")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "idx_user_blocked_keys", columnNames = {"blocker_id", "blocked_id"})
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private UserModel blocker; // Quem deu o block

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private UserModel blocked; // Quem foi bloqueado

}
