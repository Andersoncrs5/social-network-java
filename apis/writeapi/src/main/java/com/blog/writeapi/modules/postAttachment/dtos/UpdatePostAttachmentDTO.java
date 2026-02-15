package com.blog.writeapi.modules.postAttachment.dtos;

public record UpdatePostAttachmentDTO(
        String fileName,
        Boolean isVisible,
        Boolean isPublic
) {
}
