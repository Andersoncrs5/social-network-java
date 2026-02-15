package com.blog.writeapi.modules.commentAttachment.dtos;

public record UpdateCommentAttachmentDTO(
    String fileName,
    Boolean isPublic,
    Boolean isVisible
) {
}
