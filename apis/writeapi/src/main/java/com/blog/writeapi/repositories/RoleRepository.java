package com.blog.writeapi.repositories;

import com.blog.writeapi.models.RoleModel;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<@NonNull RoleModel, @NonNull Long> {
    Boolean existsByNameIgnoreCase(@NotBlank String name);
    Optional<RoleModel> findByNameIgnoreCase(@NotBlank String name);
}
