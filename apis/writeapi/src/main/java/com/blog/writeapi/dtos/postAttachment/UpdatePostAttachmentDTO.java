package com.blog.writeapi.dtos.postAttachment;

public record UpdatePostAttachmentDTO(
        String fileName,
        String contentType,
        Long fileSize
) {

}
