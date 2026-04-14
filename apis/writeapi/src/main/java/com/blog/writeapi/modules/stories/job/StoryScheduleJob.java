package com.blog.writeapi.modules.stories.job;

import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoryScheduleJob {

    private final IStoryService service;

    @Scheduled(cron = "0 */30 * * * *")
    @SchedulerLock(
            name = "StoryArchiveJob_lock",
            lockAtLeastFor = "PT5M",
            lockAtMostFor = "PT25M"
    )
    public void runArchiveJob() {
        try {
            log.info("Starting a job to archive stories...");
            service.archiveExpiredStories();
            log.info("Archiving job completed.");
        } catch (Exception e) {
            log.error("Critical failure while executing archive job: {}", e.getMessage());
        }
    }
}
