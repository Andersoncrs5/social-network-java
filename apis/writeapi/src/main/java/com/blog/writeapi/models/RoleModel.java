package com.blog.writeapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public class RoleModel {
    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(length = 100, nullable = false, unique = true, updatable = false)
    private String name;

    @Column
    private Boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRoleModel> roles;
}
