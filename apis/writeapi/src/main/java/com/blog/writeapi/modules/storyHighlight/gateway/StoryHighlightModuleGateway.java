package com.blog.writeapi.modules.storyHighlight.gateway;

import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.services.interfaces.IStorageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Component
@RequiredArgsConstructor
public class StoryHighlightModuleGateway {

    private final IUserService userService;
    private final IStorageService storageService;

    @Value("${s3.bucketAttachments}")
    private String BUCKET;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public boolean deleteObject(
            @NotBlank String key,
            String versionID
    ) {
        return storageService.deleteObject(BUCKET, key, versionID);
    }

    public boolean putObject(
            @NotBlank String key,
            @NotNull ObjectCannedACL acl,
            @NotNull @NotNull MultipartFile file,
            @NotNull UserModel user
    ) {
        return storageService.putObject(BUCKET, key, acl, file, user);
    }

}
