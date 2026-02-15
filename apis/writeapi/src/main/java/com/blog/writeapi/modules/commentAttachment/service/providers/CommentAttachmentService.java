package com.blog.writeapi.modules.commentAttachment.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.commentAttachment.dtos.CreateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.dtos.UpdateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.models.CommentAttachmentModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.commentAttachment.repository.CommentAttachmentRepository;
import com.blog.writeapi.modules.commentAttachment.service.docs.ICommentAttachmentService;
import com.blog.writeapi.utils.services.interfaces.IStorageService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
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
