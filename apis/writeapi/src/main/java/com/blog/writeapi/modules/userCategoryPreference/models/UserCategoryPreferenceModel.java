package com.blog.writeapi.modules.userCategoryPreference.models;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "user_category_preferences",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_category_preferences", columnNames = {"category_id", "user_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserCategoryPreferenceModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, updatable = false)
    private CategoryModel category;

    private Double interestScore;

}
