package com.blog.writeapi.modules.storyHighlight.dto;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryHighlightDTO extends AttachmentDTO {
    private UserDTO user;
    private String title;
}
