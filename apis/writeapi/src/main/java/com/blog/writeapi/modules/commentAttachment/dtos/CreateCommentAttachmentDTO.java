package com.blog.writeapi.modules.commentAttachment.dtos;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.bases.dto.CreateAttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentAttachmentDTO extends CreateAttachmentDTO {
    @IsId private Long commentId;
}
