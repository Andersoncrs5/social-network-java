package com.blog.writeapi.modules.apiKeys.controller.docs;

import com.blog.writeapi.modules.apiKeys.dto.CreateApiKeyDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApiKeyControllerDocs {
    @PostMapping
    ResponseHttp<String> create(@Valid @RequestBody CreateApiKeyDTO dto);
    @DeleteMapping("/{id}")
    ResponseHttp<Void> delete(
            @PathVariable @IsId Long id
    );
}
