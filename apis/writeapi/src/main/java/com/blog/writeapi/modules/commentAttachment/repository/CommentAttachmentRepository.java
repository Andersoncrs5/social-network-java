package com.blog.writeapi.modules.commentAttachment.repository;

import com.blog.writeapi.modules.commentAttachment.models.CommentAttachmentModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAttachmentRepository extends JpaRepository<@NonNull CommentAttachmentModel, @NonNull Long> {
}
