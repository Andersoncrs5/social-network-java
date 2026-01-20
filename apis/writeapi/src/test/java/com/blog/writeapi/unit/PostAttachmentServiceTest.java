package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.PostAttachmentRepository;
import com.blog.writeapi.services.providers.PostAttachmentService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

@ExtendWith(MockitoExtension.class)
public class PostAttachmentServiceTest {

    @Mock
    private PostAttachmentRepository repository;
    @Mock
    private Snowflake generator;

    @InjectMocks
    private PostAttachmentService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PostAttachmentModel attachment = new PostAttachmentModel().toBuilder()
            .id(1998780200071111111L)
            .uploader(user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

}
