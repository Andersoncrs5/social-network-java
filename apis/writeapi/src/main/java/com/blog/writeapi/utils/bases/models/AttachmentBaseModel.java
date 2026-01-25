package com.blog.writeapi.utils.bases.models;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class AttachmentBaseModel extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String fileName;

    private String contentType;

    private Long fileSize;

    private Boolean isPublic;

    private Boolean isVisible;

}
