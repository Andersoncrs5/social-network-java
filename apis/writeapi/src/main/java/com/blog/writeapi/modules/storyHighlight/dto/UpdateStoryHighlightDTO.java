package com.blog.writeapi.modules.storyHighlight.dto;

import com.blog.writeapi.utils.bases.dto.UpdateAttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateStoryHighlightDTO extends UpdateAttachmentDTO {
    private String title;
}
