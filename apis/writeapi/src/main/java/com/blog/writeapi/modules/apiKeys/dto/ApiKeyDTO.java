package com.blog.writeapi.modules.apiKeys.dto;

public record ApiKeyDTO(
        Long id,
        String serviceName,
        String rawKey,
        boolean active
) {}
