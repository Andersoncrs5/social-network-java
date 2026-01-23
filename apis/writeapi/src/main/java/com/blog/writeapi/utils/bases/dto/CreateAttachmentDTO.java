package com.blog.writeapi.utils.bases.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentDTO {
    private String storageKey;
    private String fileName;
    private String contentType;
    private Long fileSize;
}
