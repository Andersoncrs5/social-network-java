package com.blog.writeapi.modules.postAttachment.repository;

import com.blog.writeapi.modules.postAttachment.models.PostAttachmentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<@NotNull PostAttachmentModel, @NotNull Long> {
    List<PostAttachmentModel> findAllByPost(PostModel post);
}
