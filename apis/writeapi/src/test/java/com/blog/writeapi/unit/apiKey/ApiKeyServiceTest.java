package com.blog.writeapi.unit.apiKey;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.apiKeys.model.ApiKeyModel;
import com.blog.writeapi.modules.apiKeys.repository.ApiKeyRepository;
import com.blog.writeapi.modules.apiKeys.service.provider.ApiKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceTest {

    @Mock private ApiKeyRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks
    private ApiKeyService service;

    ApiKeyModel key = new ApiKeyModel().toBuilder()
            .id(1998780200074176609L)
            .keyHash("key-sh-security")
            .serviceName("sei-la.com")
            .expiresAt((OffsetDateTime.now().plusDays(12)))
            .lastUsedAt(OffsetDateTime.now())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    @DisplayName("Should generate a raw key and save its hash correctly")
    void shouldCreateApiKeySuccessfully() {
        String serviceName = "pochita-search-api";
        Long generatedId = 12345L;

        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(ApiKeyModel.class))).thenAnswer(i -> i.getArguments()[0]);

        String rawKey = service.create(serviceName);

        assertThat(rawKey).isNotNull();
        assertThat(rawKey).startsWith("sk_live_");

        ArgumentCaptor<ApiKeyModel> keyCaptor = ArgumentCaptor.forClass(ApiKeyModel.class);
        verify(repository).save(keyCaptor.capture());

        ApiKeyModel savedModel = keyCaptor.getValue();

        assertThat(savedModel.getServiceName()).isEqualTo(serviceName);
        assertThat(savedModel.getId()).isEqualTo(generatedId);
        assertThat(savedModel.isActive()).isTrue();

        String expectedHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rawKey);
        assertThat(savedModel.getKeyHash()).isEqualTo(expectedHash);

        assertThat(savedModel.getKeyHash()).isNotEqualTo(rawKey);

        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return true when raw key is valid and active")
    void shouldValidateActiveApiKey() {
        String rawKey = "sk_live_secret_123";
        String hashedKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rawKey);

        when(repository.findByKeyHashAndActiveTrue(hashedKey))
                .thenReturn(Optional.of(key));

        boolean isValid = service.validate(rawKey);

        assertThat(isValid).isTrue();
        verify(repository, times(1)).findByKeyHashAndActiveTrue(hashedKey);
    }

    @Test
    @DisplayName("Should return false when raw key does not match any active hash")
    void shouldNotValidateInvalidApiKey() {
        String invalidRawKey = "chave_errada_123";
        String hashedInvalidKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(invalidRawKey);

        when(repository.findByKeyHashAndActiveTrue(hashedInvalidKey))
                .thenReturn(Optional.empty());

        boolean isValid = service.validate(invalidRawKey);

        assertThat(isValid).isFalse();
        verify(repository, times(1)).findByKeyHashAndActiveTrue(hashedInvalidKey);
    }

    @Test
    @DisplayName("Should return false when raw key is null or empty")
    void shouldNotValidateEmptyKey() {
        assertThat(service.validate(null)).isFalse();
        assertThat(service.validate("")).isFalse();

        verifyNoInteractions(repository);
    }

}
