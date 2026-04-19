package com.blog.writeapi.modules.stories.repository;

import com.blog.writeapi.modules.stories.model.StoryModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface StoryRepository extends JpaRepository<@NonNull StoryModel, @NonNull Long> {
    @Modifying
    @Query("UPDATE StoryModel s SET s.isArchived = true WHERE s.expiresAt < :now AND s.isArchived = false")
    int archiveAllExpired(@Param("now") OffsetDateTime now);

    Page<StoryModel> findAllByExpiresAtBeforeAndIsArchivedFalse(OffsetDateTime now, Pageable pageable);

    @Query("""
            SELECT s FROM StoryModel s
            WHERE s.expiresAt < :now
            AND s.isArchived = false
            AND s.isHighlight = false
        """)
    Page<StoryModel> findExpiredStoriesToArchive(
            @Param("now") OffsetDateTime now,
            Pageable pageable
    );
}
