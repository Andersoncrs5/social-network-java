package com.blog.writeapi.modules.stories.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.gateway.StoryModuleGateway;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.repository.StoryRepository;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.StoryMapper;
import com.blog.writeapi.utils.services.providers.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StoryService implements IStoryService {

    private final StoryRepository repository;
    private final StoryModuleGateway gateway;
    private final StoryMapper mapper;
    private final StorageService storageService;
    private final Snowflake generator;

    @Value("${s3.bucketAttachments}")
    private String BUCKET;

    public StoryModel create(
            @IsId Long userId,
            CreateStoryDTO dto
    ) {
        UserModel user = this.gateway.findUserById(userId);
        StoryModel model = this.mapper.toModel(dto);

        model.setStorageKey(UUID.randomUUID().toString());
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;
        Boolean object = this.storageService.putObject(BUCKET, model.getStorageKey(), acl, dto.getFile(), user);

        if (!object) {
            throw new BusinessRuleException("Error the send file to aws s3");
        }

        model.setId(generator.nextId());
        model.setUser(user);
        model.setExpiresAt(OffsetDateTime.now().plusHours(24));
        model.setFileSize(dto.getFile().getSize());

        return repository.save(model);
    }

    public boolean delete(@IsModelInitialized StoryModel story) {
        Boolean deleted = this.storageService.deleteObject(BUCKET, story.getStorageKey(), null);

        if (!deleted)
            return false;

        repository.delete(story);

        return true;
    }

    public StoryModel findById(@IsId Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Story not found"));
    }

    public void archiveExpiredStories() {
        int updatedCount = repository.archiveAllExpired(OffsetDateTime.now());
        log.info("Archiving complete. Total stories processed: {}", updatedCount);
    }

    public void archiveExpiredStoriesPageable() {
        OffsetDateTime now = OffsetDateTime.now();
        Pageable pageable = PageRequest.of(0, 100);

        Page<StoryModel> expiredPage;

        do {
            expiredPage = repository.findAllByExpiresAtBeforeAndIsArchivedFalse(now, pageable);

            expiredPage.forEach(story -> story.setArchived(true));

            repository.saveAll(expiredPage);
            repository.flush();

        } while (expiredPage.hasNext());
    }

}
