package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostTagModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<@NonNull PostTagModel, @NonNull Long> {
    Boolean existsByPostIdAndTagId(@IsId Long postId, @IsId Long tagId);
}
