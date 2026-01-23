package com.blog.writeapi.utils.bases.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;


@Getter
@Setter
public class BaseDTO {
    private Long id;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
