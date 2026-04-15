package com.blog.writeapi.modules.stories.job;

import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.repository.StoryRepository;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoryExpirationWorker {

    private final StoryRepository storyRepository;
    private final IStoryService service;

    @Scheduled(cron = "0 0 0,12 * * *")    @Transactional
    public void cleanupExpiredStories() {
        OffsetDateTime now = OffsetDateTime.now();
        int pageSize = 100;

        Page<StoryModel> expiredPage;

        do {
            expiredPage = storyRepository.findExpiredStoriesToArchive(now, PageRequest.of(0, pageSize));

            List<StoryModel> stories = expiredPage.getContent();

            if (!stories.isEmpty()) {
                stories.forEach(story -> {
                    try {
                        service.delete(story);
                    } catch (Exception e) {
                        log.error("Erro ao deletar story {}: {}", story.getId(), e.getMessage());
                    }
                });
            } else {
                break;
            }

        } while (expiredPage.hasNext());
    }

}
