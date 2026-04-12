package com.blog.writeapi.modules.apiKeys.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateApiKeyDTO(
        @NotBlank
        String serviceName
) {
}
