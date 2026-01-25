package com.blog.writeapi.dtos.postAttachment;

public record UpdatePostAttachmentDTO(
        String fileName,
        Boolean isVisible,
        Boolean isPublic
) {
}
