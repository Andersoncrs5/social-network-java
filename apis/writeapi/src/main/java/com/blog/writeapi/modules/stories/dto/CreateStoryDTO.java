package com.blog.writeapi.modules.stories.dto;

import com.blog.writeapi.utils.bases.dto.CreateAttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStoryDTO extends CreateAttachmentDTO {
    private String caption;
    private String backgroundColor;
}
