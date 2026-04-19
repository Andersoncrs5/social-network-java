package com.blog.writeapi.modules.StoryHighlightItem.repository;

import com.blog.writeapi.modules.StoryHighlightItem.model.StoryHighlightItemModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoryHighlightItemRepository extends JpaRepository<StoryHighlightItemModel, Long> {
    boolean existsByStoryIdAndHighlightIdAndUserId(
            @IsId Long storyId,
            @IsId Long highlightId,
            @IsId Long userId
    );


    @Modifying
    @Query("DELETE FROM StoryHighlightItemModel s WHERE s.id = :id")
    int deleteByID(@Param("id") Long id);

    Optional<StoryHighlightItemModel> findByStoryIdAndHighlightIdAndUserId(
            @IsId Long storyId,
            @IsId Long highlightId,
            @IsId Long userId
    );

}
