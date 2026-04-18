package com.blog.writeapi.modules.storyHighlight.dto;

import com.blog.writeapi.utils.annotations.validations.image.fileContentType.FileContentType;
import com.blog.writeapi.utils.bases.dto.CreateAttachmentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateStoryHighlightDTO extends CreateAttachmentDTO {

    private String title;

    @Override
    @FileContentType(
            allowed = {"image/jpeg", "image/png", "image/webp"},
            message = "Only JPEG, PNG, or WEBP images are accepted."
    )
    public MultipartFile getFile() {
        return super.getFile();
    }
}
