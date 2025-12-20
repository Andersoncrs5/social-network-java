package com.blog.writeapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, unique = true, nullable = false)
    private String username;

    @Column(length = 150, unique = true, nullable = false)
    private String email;

    @Column(length = 300, nullable = false)
    private String password = "";

    @Column(length = 500)
    private String refreshToken = "";

    @Version
    private Long version;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Column
    private OffsetDateTime loginBlockAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRoleModel> roles;


}
