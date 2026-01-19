package com.blog.writeapi.repositories;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.emailConstraint.EmailConstraint;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull UserModel, @NonNull Long> {
    Optional<UserModel> findByEmail(@EmailConstraint String email);

    boolean existsByEmail(@EmailConstraint String email);

    boolean existsByUsername(String username);
}