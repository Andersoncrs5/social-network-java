package com.blog.writeapi.dtos.postAttachment;

import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAttachmentDTO extends AttachmentDTO {
    private PostDTO post;
    private UserDTO uploader;
}
