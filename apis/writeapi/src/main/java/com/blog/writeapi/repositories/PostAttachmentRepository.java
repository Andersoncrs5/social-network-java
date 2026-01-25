package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.PostModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<@NotNull PostAttachmentModel, @NotNull Long> {
    List<PostAttachmentModel> findAllByPost(PostModel post);
}
