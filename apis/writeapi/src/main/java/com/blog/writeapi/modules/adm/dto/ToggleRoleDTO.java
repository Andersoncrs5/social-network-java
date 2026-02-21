package com.blog.writeapi.modules.adm.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.validation.constraints.NotBlank;

public record ToggleRoleDTO(
        @NotBlank String roleName,
        @IsId Long userId
) {
}
