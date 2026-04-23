package com.blog.writeapi.modules.apiKeys.controller.provider;

import com.blog.writeapi.modules.apiKeys.controller.docs.ApiKeyControllerDocs;
import com.blog.writeapi.modules.apiKeys.dto.CreateApiKeyDTO;
import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import com.blog.writeapi.modules.apiKeys.service.provider.ApiKeyService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/admin/api-keys")
public class ApiKeyController implements ApiKeyControllerDocs {

    private final ApiKeyService service;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseHttp<String> create(@Valid @RequestBody CreateApiKeyDTO dto) {
        String rawKey = service.create(dto.serviceName());

        return new ResponseHttp<>(
            rawKey,
            "Api Hash created!",
            UUID.randomUUID().toString(),
            1,
            true,
            OffsetDateTime.now()
        );
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseHttp<Void> delete(
            @PathVariable @IsId Long id
    ) {
        this.service.deleteAndCount(id);

        return ResponseHttp.success(
                null,
                "Api Hash deleted!",
                UUID.randomUUID().toString()
        );
    }

    @GetMapping("internal/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Authenticated successfully with API Key!");
    }

}