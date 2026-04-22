package com.blog.writeapi.modules.StoryHighlightItem.service.provider;

import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.modules.StoryHighlightItem.gateway.StoryHighlightItemModuleGateway;
import com.blog.writeapi.modules.StoryHighlightItem.model.StoryHighlightItemModel;
import com.blog.writeapi.modules.StoryHighlightItem.repository.StoryHighlightItemRepository;
import com.blog.writeapi.modules.StoryHighlightItem.service.interfaces.IStoryHighlightItemService;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.result.Error;
import com.blog.writeapi.utils.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class StoryHighlightItemService implements IStoryHighlightItemService {

    private final StoryHighlightItemRepository repository;
    private final StoryHighlightItemModuleGateway gateway;

    public Result<StoryHighlightItemModel> create(
            @IsId Long userId,
            CreateStoryHighlightItemDTO dto
    ) {
        var highlight = gateway.findHighlightById(dto.highlightId());

        if (!Objects.equals(highlight.getUser().getId(), userId))
            return Result.conflict("This highlight is not your.");

        var story = gateway.findStoryById(dto.storyId());

        if (!Objects.equals(story.getUser().getId(), userId))
            return Result.forb("This story is not your!");

        var user = gateway.findUserById(userId);

        if (repository.existsByStoryIdAndHighlightIdAndUserId(dto.storyId(), dto.highlightId(), userId)) {
            return Result.conflict("This item has already been added to this highlight.");
        }

        StoryHighlightItemModel item = StoryHighlightItemModel.builder()
                .user(user)
                .story(story)
                .highlight(highlight)
                .build();

        try {
            StoryHighlightItemModel save = repository.save(item);
            this.gateway.toggleStoryHighlight(story);
            return Result.created(save);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (message.contains("uk_story_highlight_items_user_story_highlight")) {
                return Result.conflict("This item has already been added to this highlight.");
            }
            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    public Result<Void> deleteById(@IsId Long id) {
        int result = repository.deleteByID(id);

        if (Objects.equals(result, 0)) {
            return Result.failure(HttpStatus.NOT_FOUND, "", "Story highlight item not found");
        }

        return Result.success(null);
    }

    @Transactional
    public ResultToggle<StoryHighlightItemModel> toggle(
            @IsId Long userId,
            CreateStoryHighlightItemDTO dto
    ) {
        Optional<StoryHighlightItemModel> optional = this.repository.findByStoryIdAndHighlightIdAndUserId(dto.storyId(), dto.highlightId(), userId);

        if (optional.isPresent()) {
            StoryHighlightItemModel model = optional.get();
            repository.deleteByID(model.getId());
            return ResultToggle.removed();
        }

        Result<StoryHighlightItemModel> result = this.create(userId, dto);

        if (result.isFailure()) {
            throw new BusinessRuleException(result.getError().message(), result.getStatus());
        }

        return ResultToggle.added(result.getValue());
    }

}
