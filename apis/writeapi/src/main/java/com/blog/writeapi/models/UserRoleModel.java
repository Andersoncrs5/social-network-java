package com.blog.writeapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_role_id", columnList = "role_id"),
        @Index(name = "uk_user_role", columnList = "user_id, role_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleModel {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false, updatable = false)
    private RoleModel role;

    @CreatedDate
    private LocalDateTime createdAt;

}
