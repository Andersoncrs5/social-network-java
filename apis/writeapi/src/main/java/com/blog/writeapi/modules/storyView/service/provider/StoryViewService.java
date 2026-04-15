package com.blog.writeapi.modules.storyView.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyView.gateway.StoryViewModuleGateway;
import com.blog.writeapi.modules.storyView.model.StoryViewModel;
import com.blog.writeapi.modules.storyView.repository.StoryViewRepository;
import com.blog.writeapi.modules.storyView.service.interfaces.IStoryViewService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StoryViewService implements IStoryViewService {

    private final StoryViewRepository repository;
    private final Snowflake snowflake;
    private final StoryViewModuleGateway gateway;

    public StoryViewModel findByIdSimple(@IsId Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Story view not found"));
    }

    public void delete(@IsId Long id) {
        int rowsDeleted = repository.deleteAndCount(id);
        if (rowsDeleted == 0) {
            throw new ModelNotFoundException("StoryView not found with id: " + id);
        }
    }

    public StoryViewModel createView(
            @IsId Long userId,
            @IsId Long storyId
    ) {
        UserModel user = gateway.findUserById(userId);
        StoryModel story = gateway.findStoryById(storyId);

        StoryViewModel build = new StoryViewModel().toBuilder()
                .id(snowflake.nextId())
                .user(user)
                .story(story)
                .build();

        try {
            return repository.save(build);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_story_views_user_story")) {
                throw new UniqueConstraintViolationException(
                        "You already view this story"
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating PostReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    public boolean createIfNotExists(@IsId Long userId, @IsId Long storyId) {
        boolean exists = repository.existsByUserIdAndStoryId(userId, storyId);

        if (!exists) {
            createView(userId, storyId);
            return true;
        }

        return false;
    }

}
