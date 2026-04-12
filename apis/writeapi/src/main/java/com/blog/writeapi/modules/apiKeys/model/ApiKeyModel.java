package com.blog.writeapi.modules.apiKeys.model;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "api_keys",
        indexes = {
            @Index(name = "idx_api_key_hash", columnList = "key_hash")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApiKeyModel extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String keyHash;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private boolean active = true;

    private OffsetDateTime expiresAt;
    private OffsetDateTime lastUsedAt;

    String hashKey() {
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.keyHash);
    }
}