package com.blog.writeapi.repositories;

import com.blog.writeapi.models.UserModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull UserModel, @NonNull Long> {
    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

}