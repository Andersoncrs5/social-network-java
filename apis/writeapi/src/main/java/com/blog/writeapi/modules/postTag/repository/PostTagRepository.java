package com.blog.writeapi.modules.postTag.repository;

import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostTagRepository extends JpaRepository<@NonNull PostTagModel, @NonNull Long> {
    Boolean existsByPostIdAndTagId(@IsId Long postId, @IsId Long tagId);

    @Modifying
    @Query("DELETE FROM PostTagModel e WHERE e.id = :id")
    int deleteAndCount(@Param("id") @IsId Long id);

}
