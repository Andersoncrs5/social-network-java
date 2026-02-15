package com.blog.writeapi.dtos.commentAttachment;

import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAttachmentDTO extends AttachmentDTO {
    private CommentDTO comment;
    private UserDTO uploader;
}
