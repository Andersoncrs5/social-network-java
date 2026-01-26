package com.blog.writeapi.utils.res.swagger.postAttachment;

import com.blog.writeapi.dtos.postAttachment.PostAttachmentDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponsePostAttachmentDTO(
        ResponseHttp<PostAttachmentDTO> dto
){
}
