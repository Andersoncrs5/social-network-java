package com.blog.writeapi.modules.apiKeys.repository;

import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyModel, Long> {
    Optional<ApiKeyModel> findByKeyHashAndActiveTrue(@NotBlank String keyHash);

    @Modifying
    @Query("DELETE FROM ApiKeyModel s WHERE s.id = :id")
    int deleteAndCount(@Param("id") Long id);

}
