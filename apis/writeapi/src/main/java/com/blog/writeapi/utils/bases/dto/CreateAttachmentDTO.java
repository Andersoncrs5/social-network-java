package com.blog.writeapi.utils.bases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateAttachmentDTO {
    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;

    @NotNull
    private Boolean isPublic;

    private Boolean isVisible;

    @Schema(description = "File to be uploaded", type = "string", format = "binary")
    @NotNull
    private MultipartFile file;
}
