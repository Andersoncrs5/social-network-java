package com.blog.writeapi.dtos.commentAttachment;

public record UpdateCommentAttachmentDTO(
    String fileName,
    Boolean isPublic,
    Boolean isVisible
) {
}
