package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentAttachmentModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAttachmentRepository extends JpaRepository<@NonNull CommentAttachmentModel, @NonNull Long> {
}
