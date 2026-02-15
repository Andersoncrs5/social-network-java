package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.commentAttachment.CreateCommentAttachmentDTO;
import com.blog.writeapi.dtos.commentAttachment.UpdateCommentAttachmentDTO;
import com.blog.writeapi.models.CommentAttachmentModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentAttachmentRepository;
import com.blog.writeapi.services.interfaces.ICommentAttachmentService;
import com.blog.writeapi.services.interfaces.IStorageService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentAttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAttachmentService implements ICommentAttachmentService {

    private final CommentAttachmentRepository repository;
    private final IStorageService storageService;
    private final Snowflake generator;
    private final CommentAttachmentMapper mapper;

    @Value("${s3.bucketAttachments}")
    private String BUCKET;

    @Override
    public CommentAttachmentModel findByIdSimple(@IsId Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Attachment not found"));
    }

    @Override
    public boolean delete(@IsModelInitialized CommentAttachmentModel attachment) {
        Boolean deleted = this.storageService.deleteObject(BUCKET, attachment.getStorageKey(), null);

        if (!deleted)
            return false;

        this.repository.delete(attachment);

        return true;
    }

    public Optional<CommentAttachmentModel> create(
            CreateCommentAttachmentDTO dto,
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
            ) {
        CommentAttachmentModel attachment = this.mapper.toModel(dto);
        attachment.setStorageKey(UUID.randomUUID().toString());

        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;

        Boolean object = this.storageService
                .putObject(BUCKET, attachment.getStorageKey(), acl, dto.getFile(), user);

        if (!object) { return Optional.empty(); }

        attachment.setId(generator.nextId());
        attachment.setUploader(user);
        attachment.setComment(comment);
        attachment.setFileSize(dto.getFile().getSize());

        return Optional.of(repository.save(attachment));
    }

    public CommentAttachmentModel update(@IsModelInitialized CommentAttachmentModel model, UpdateCommentAttachmentDTO dto) {
        this.mapper.merge(dto, model);

        return repository.save(model);
    }

}
