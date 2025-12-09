package com.blog.writeapi.repositories;

import com.blog.writeapi.models.RoleModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<@NonNull RoleModel, @NonNull Long> {
    Boolean existsByName(String name);
    Optional<RoleModel> findByName(String name);
}
