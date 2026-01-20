package com.blog.writeapi.models;

import com.blog.writeapi.utils.bases.models.AttachmentBaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "post_attachments",
        indexes = {
                @Index(name = "idx_post_attachments_post_id", columnList = "post_id"),
                @Index(name = "idx_post_attachments_uploader_id", columnList = "uploader_id"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostAttachmentModel extends AttachmentBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private PostModel post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false, updatable = false)
    private UserModel uploader;

}
