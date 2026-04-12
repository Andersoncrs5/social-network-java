package com.blog.writeapi.modules.apiKeys.repository;

import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyModel, Long> {
    Optional<ApiKeyModel> findByKeyHashAndActiveTrue(@NotBlank String keyHash);
}
