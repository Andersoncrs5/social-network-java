package com.blog.writeapi.dtos.commentAttachment;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.bases.dto.CreateAttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentAttachmentDTO extends CreateAttachmentDTO {
    @IsId private Long commentId;
}
