package com.blog.writeapi.modules.storyHighlight.repository;

import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryHighlightRepository extends JpaRepository<StoryHighlightModel, Long> {

    @Modifying
    @Query("DELETE FROM StoryHighlightModel e WHERE e.id = :id")
    int deleteAndCount(@Param("id") @IsId Long id);

}
