package com.blog.writeapi.utils.bases.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class BaseDTO {
    private Long id;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
