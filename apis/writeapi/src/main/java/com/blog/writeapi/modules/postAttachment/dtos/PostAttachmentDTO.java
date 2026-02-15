package com.blog.writeapi.modules.postAttachment.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAttachmentDTO extends AttachmentDTO {
    private PostDTO post;
    private UserDTO uploader;
}
