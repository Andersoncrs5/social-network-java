package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostAttachmentModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAttachmentRepository extends JpaRepository<@NotNull PostAttachmentModel, @NotNull Long> {
}
