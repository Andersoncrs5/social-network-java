package com.blog.writeapi.modules.commentAttachment.dtos;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAttachmentDTO extends AttachmentDTO {
    private CommentDTO comment;
    private UserDTO uploader;
}
