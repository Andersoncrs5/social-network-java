package com.blog.writeapi.modules.storyView.repository;

import com.blog.writeapi.modules.storyView.model.StoryViewModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StoryViewRepository extends JpaRepository<StoryViewModel, Long> {

    @Query("SELECT sv FROM StoryViewModel sv " +
            "JOIN FETCH sv.user " +
            "JOIN FETCH sv.story " +
            "WHERE sv.id = :id"
    )
    Optional<StoryViewModel> findByIdWithUserAndStory(@Param("id") @IsId Long id);

    @Query("SELECT sv FROM StoryViewModel sv " +
            "JOIN FETCH sv.user " +
            "WHERE sv.story.id = :storyId"
    )
    List<StoryViewModel> findAllByStoryIdWithUser(@Param("storyId") @IsId Long storyId);

    @EntityGraph(attributePaths = {"user", "story"})
    Optional<StoryViewModel> findWithGraphById(@IsId Long id);

    @Modifying
    @Query("DELETE FROM StoryViewModel sv WHERE sv.id = :id")
    int deleteAndCount(@Param("id") @IsId Long id);

    boolean existsByUserIdAndStoryId(@IsId Long userId, @IsId Long storyId);

}