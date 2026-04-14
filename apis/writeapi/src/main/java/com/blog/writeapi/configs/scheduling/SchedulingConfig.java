package com.blog.writeapi.configs.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("story-job-pool-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        taskScheduler.setAwaitTerminationSeconds(30);
        taskScheduler.initialize();
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskRegistrar.setTaskScheduler(taskScheduler);
        taskScheduler.setErrorHandler(throwable -> {
            log.error("Error critical in Scheduling: {}", throwable.getMessage());

        });
    }
}