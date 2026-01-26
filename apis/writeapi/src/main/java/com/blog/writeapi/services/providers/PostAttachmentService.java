package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.dtos.postAttachment.UpdatePostAttachmentDTO;
import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.PostAttachmentRepository;
import com.blog.writeapi.services.interfaces.IPostAttachmentService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostAttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostAttachmentService implements IPostAttachmentService {

    private final PostAttachmentRepository repository;
    private final StorageService storageService;
    private final PostAttachmentMapper mapper;
    private final Snowflake generator;

    @Value("${s3.bucketAttachments}")
    private String BUCKET;

    @Transactional(readOnly = true)
    public PostAttachmentModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Attachment not found"));
    }

    public Boolean delete(@IsModelInitialized PostAttachmentModel model) {
        Boolean exists = this.storageService.deleteObject(BUCKET, model.getStorageKey(), null);
        if (!exists)
            return false;

        this.repository.delete(model);

        return true;
    }

    public Optional<PostAttachmentModel> create(CreatePostAttachmentDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post) {
        PostAttachmentModel model = this.mapper.toModel(dto);
        model.setStorageKey(UUID.randomUUID() + UUID.randomUUID().toString());

        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;

        Boolean object = this.storageService.putObject(BUCKET, model.getStorageKey(), acl, dto.getFile(), user);

        if (!object) {
            return Optional.empty();
        }

        model.setId(generator.nextId());
        model.setUploader(user);
        model.setPost(post);
        model.setFileSize(dto.getFile().getSize());

        return Optional.of(repository.save(model));
    }

    @Transactional
    public PostAttachmentModel updateMetadata(@IsId Long id, UpdatePostAttachmentDTO dto) {
        PostAttachmentModel model = getByIdSimple(id);

        this.mapper.merge(dto, model);

        return repository.save(model);
    }

    @Transactional
    public void deleteAllByPost(@IsModelInitialized PostModel post) {
        List<PostAttachmentModel> attachments = repository.findAllByPost(post);
        List<String> keys = attachments.stream().map(PostAttachmentModel::getStorageKey).toList();

        this.storageService.deleteMultiObject(BUCKET, keys);
        this.repository.deleteAllInBatch(attachments);
    }

}
