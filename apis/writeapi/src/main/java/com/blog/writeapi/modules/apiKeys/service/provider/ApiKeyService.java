package com.blog.writeapi.modules.apiKeys.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.configs.SecretGenerator;
import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import com.blog.writeapi.modules.apiKeys.repository.ApiKeyRepository;
import com.blog.writeapi.modules.apiKeys.service.interfaces.IApiKeyService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ApiKeyService implements IApiKeyService {
    private final ApiKeyRepository repository;
    private final Snowflake generator;

    public String create(String serviceName) {
        String rawKey = SecretGenerator.generate("sk_live");

        String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rawKey);

        ApiKeyModel model = ApiKeyModel.builder()
                .id(generator.nextId())
                .serviceName(serviceName)
                .keyHash(hash)
                .active(true)
                .build();

        repository.save(model);

        return rawKey;
    }

    public boolean validate(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) return false;

        String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rawKey);
        return repository.findByKeyHashAndActiveTrue(hash).isPresent();
    }

    public void delete(@IsModelInitialized ApiKeyModel key) {
        repository.delete(key);
    }

    public ApiKeyModel findByIdSimple(@IsId Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("API Key not found"));
    }

}
