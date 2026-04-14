package com.blog.writeapi.modules.stories.dto;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.bases.dto.AttachmentDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class StoryDTO extends AttachmentDTO {
    private String caption;
    private String backgroundColor;
    private UserDTO user;
    OffsetDateTime expiresAt;
    boolean isArchived;
}
