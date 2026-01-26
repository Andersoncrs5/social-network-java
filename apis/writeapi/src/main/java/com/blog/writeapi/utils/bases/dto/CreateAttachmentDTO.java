package com.blog.writeapi.utils.bases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateAttachmentDTO {
    private String fileName;
    private String contentType;
    private Boolean isPublic;
    private Boolean isVisible;

    @Schema(description = "File to be uploaded", type = "string", format = "binary")
    @NotBlank
    private MultipartFile file;
}
