package com.blog.writeapi.modules.apiKeys.service.interfaces;

import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.validation.constraints.NotBlank;

public interface IApiKeyService {
    String create(@NotBlank String serviceName);
    ApiKeyModel findByIdSimple(@IsId Long id);
    void deleteAndCount(@IsId Long id);
}
