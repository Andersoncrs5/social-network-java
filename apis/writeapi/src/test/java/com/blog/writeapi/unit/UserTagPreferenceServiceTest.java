package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.models.UserTagPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserTagPreferenceRepository;
import com.blog.writeapi.services.providers.UserTagPreferenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserTagPreferenceServiceTest {

    @Mock private UserTagPreferenceRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks
    private UserTagPreferenceService service;

    TagModel tag = new TagModel().toBuilder()
            .id(1998780200074176609L)
            .name("springBoot")
            .slug("spring-boot")
            .description("AnyDesc")
            .isActive(true)
            .isVisible(true)
            .isSystem(false)
            .postsCount(0L)
            .version(1L)
            .lastUsedAt(OffsetDateTime.now())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserTagPreferenceModel preference = new UserTagPreferenceModel().toBuilder()
            .id(1998780200071111111L)
            .user(user)
            .tag(tag)
            .interestScore(37.9)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnUserTagPreferenceWhenGetByUserAndTag() {
        when(repository.findByUserAndTag(user, tag))
                .thenReturn(Optional.of(preference));

        Optional<UserTagPreferenceModel> optional = this.service.getByUserAndTag(user, tag);

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findByUserAndTag(user, tag);
    }

    @Test
    void shouldReturnNullWhenGetByUserAndTag() {
        when(repository.findByUserAndTag(user, tag))
                .thenReturn(Optional.empty());

        Optional<UserTagPreferenceModel> optional = this.service.getByUserAndTag(user, tag);

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByUserAndTag(user, tag);
    }

    @Test
    void shouldDeleteUserTagPreference() {
        doNothing().when(repository).delete(preference);

        this.service.delete(preference);

        verify(repository, times(1)).delete(preference);
    }

    @Test
    void shouldCreatePreference() {
        when(this.generator.nextId())
                .thenReturn(this.preference.getId());
        when(repository.save(any(UserTagPreferenceModel.class)))
                .thenReturn(this.preference);

        UserTagPreferenceModel model = service.create(user, tag);
        assertThat(model.getId()).isEqualTo(preference.getId());

        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any(UserTagPreferenceModel.class));
    }

}
