package com.blog.writeapi.dtos.postAttachment;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.bases.dto.CreateAttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostAttachmentDTO extends CreateAttachmentDTO {
    @IsId
    private Long postId;
}
