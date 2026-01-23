package com.blog.writeapi.utils.bases.dto;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AttachmentDTO extends BaseDTO {
    private String storageKey;
    private String fileName;
    private String contentType;
    private Long fileSize;
}
