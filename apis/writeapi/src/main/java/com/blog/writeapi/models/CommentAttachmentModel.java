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
        name = "comment_attachments",
        indexes = {
                @Index(name = "idx_comment_attachments_comment_id", columnList = "comment_id"),
                @Index(name = "idx_comment_attachments_uploader_id", columnList = "uploader_id"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentAttachmentModel extends AttachmentBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false, updatable = false)
    private CommentModel comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false, updatable = false)
    private UserModel uploader;

}
