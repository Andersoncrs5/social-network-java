package com.blog.writeapi.modules.adm.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record ToggleRoleAdmDTO(
        @IsId Long userId
) {
}
