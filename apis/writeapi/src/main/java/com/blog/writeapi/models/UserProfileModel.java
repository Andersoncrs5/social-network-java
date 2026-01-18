package com.blog.writeapi.models;

import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles", indexes = {
        @Index(name = "idx_user_id_profiles", columnList = "user_id"),
        @Index(name = "idx_visibility_profiles", columnList = "visibility"),
})
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileModel extends BaseEntity {

    @Column(length = 800)
    private String bio = "no bio";

    @Column(length = 500)
    private String avatarUrl = "";

    @ElementCollection
    @CollectionTable(
            name = "profile_websites",
            joinColumns = @JoinColumn(name = "profile_id"),
            indexes = @Index(name = "idx_profile_websites_id", columnList = "profile_id")
    )
    @Column(name = "url", length = 500)
    private Set<String> websiteUrls = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20, nullable = false)
    private ProfileVisibilityEnum visibility = ProfileVisibilityEnum.PUBLIC;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;

}
