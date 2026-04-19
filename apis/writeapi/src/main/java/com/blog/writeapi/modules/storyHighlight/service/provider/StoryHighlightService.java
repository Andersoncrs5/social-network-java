package com.blog.writeapi.modules.storyHighlight.service.provider;

import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.gateway.StoryHighlightModuleGateway;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.storyHighlight.repository.StoryHighlightRepository;
import com.blog.writeapi.modules.storyHighlight.service.interfaces.IStoryHighlightService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.StoryHighlightMapper;
import com.blog.writeapi.utils.result.Result;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class StoryHighlightService implements IStoryHighlightService {

    private final StoryHighlightRepository repository;
    private final StoryHighlightModuleGateway gateway;
    private final StoryHighlightMapper mapper;

    private static final List<String> ALLOWED_IMAGES = List.of("image/jpeg", "image/png", "image/webp");

    private boolean isInvalidImage(MultipartFile file) {
        return file == null || file.getContentType() == null ||
                !ALLOWED_IMAGES.contains(file.getContentType().toLowerCase());
    }

    public StoryHighlightModel findByIdSimple(@IsId Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Story Highlight not found"));
    }

    public boolean delete(
            @IsModelInitialized StoryHighlightModel highlight
    ) {
        boolean deleted = gateway.deleteObject(highlight.getStorageKey(), null);

        if (!deleted)
            return false;

        repository.delete(highlight);

        return true;
    }

    public StoryHighlightModel create(
            @IsId Long userId,
            @NotNull CreateStoryHighlightDTO dto
    ) {
        UserModel user = this.gateway.findUserById(userId);
        StoryHighlightModel model = this.mapper.toModel(dto);

        model.setStorageKey(UUID.randomUUID().toString());
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;

        boolean putted = this.gateway.putObject(model.getStorageKey(), acl, dto.getFile(), user);
        if (!putted) throw new BusinessRuleException("Error the send file to aws s3");

        model.setUser(user);
        model.setFileSize(dto.getFile().getSize());

        return repository.save(model);
    }

    public Result<StoryHighlightModel> update(
            @IsModelInitialized UserModel user,
            @IsModelInitialized StoryHighlightModel highlight,
            @NotNull UpdateStoryHighlightDTO dto
    ) {

        boolean deleted = gateway.deleteObject(highlight.getStorageKey(), null);

        if (!deleted) {
            return Result.failure(HttpStatus.BAD_REQUEST, "AWS.DeleteError", "Error deleting file from AWS");
        }

        this.mapper.merge(dto, highlight);

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            if (isInvalidImage(dto.getFile())) {
                throw new BusinessRuleException("Just image (JPEG, PNG, WEBP)");
            }

            highlight.setStorageKey(UUID.randomUUID().toString());
            highlight.setFileSize(dto.getFile().getSize());

            ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ : ObjectCannedACL.PRIVATE;

            boolean uploaded = this.gateway.putObject(highlight.getStorageKey(), acl, dto.getFile(), user);

            if (!uploaded) {
                return Result.failure(HttpStatus.BAD_REQUEST, "AWS.UploadError", "Error uploading file to AWS");
            }
        }

        return Result.ok(repository.save(highlight));
    }

}
