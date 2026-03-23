package com.blog.writeapi.unit.userSettings;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.gateway.UserSettingsModuleGateway;
import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.modules.userSettings.model.enums.ContentFilterLevelEnum;
import com.blog.writeapi.modules.userSettings.model.enums.FontSizeScaleEnum;
import com.blog.writeapi.modules.userSettings.model.enums.LanguageEnum;
import com.blog.writeapi.modules.userSettings.model.enums.ThemeEnum;
import com.blog.writeapi.modules.userSettings.repository.UserSettingsRepository;
import com.blog.writeapi.modules.userSettings.service.provider.UserSettingsService;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.UserSettingsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSettingsServiceTest {

    @Mock private UserSettingsRepository repository;
    @Mock private Snowflake generator;
    @Mock private UserSettingsMapper mapper;
    @Mock private UserSettingsModuleGateway gateway;
    @InjectMocks private UserSettingsService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .build();

    UserSettingsModel settings = new UserSettingsModel().toBuilder()
            .id(1998780211111111219L)
            .showOnlineStatus(false)
            .notifyNewFollower(false)
            .notifyComments(false)
            .notifyLikes(false)
            .notifyMentions(false)
            .twoFactorEnabled(false)
            .autoplayVideos(false)
            .marketingEmailsAllowed(false)
            .itemsPerPage(10)
            .contentFilterLevel(ContentFilterLevelEnum.MODERATE)
            .fontSizeScale(FontSizeScaleEnum.SMALL)
            .language(LanguageEnum.EN_US)
            .theme(ThemeEnum.SYSTEM)
            .timezone("UTC")
            .user(user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnUserSettingsWhenGetByIdSimple() {
        when(repository.findByUserId(user.getId()))
                .thenReturn(Optional.of(settings));

        UserSettingsModel model = this.service.findByUserIdSimple(user.getId());

        verify(repository, times(1)).findByUserId(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowModelNotFoundExceptionWhenGetById() {
        when(repository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUserIdSimple(user.getId()))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("User setting not found");

        verify(repository, times(1)).findByUserId(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateUserSetting() {
        when(gateway.findUserById(user.getId()))
                .thenReturn(user);
        when(generator.nextId())
                .thenReturn(settings.getId());
        when(repository.save(any()))
                .thenReturn(settings);

        UserSettingsModel model = this.service.create(user.getId());

        assertThat(model.getId())
                .isEqualTo(settings.getId());

        verifyNoMoreInteractions(repository, gateway, generator);

        InOrder order = inOrder(repository, gateway, generator);

        order.verify(gateway).findUserById(user.getId());
        order.verify(generator).nextId();
        order.verify(repository).save(any());
    }

    @Test
    void shouldUpdateUserSetting() {
        UpdateUserSettingsDTO dto = new UpdateUserSettingsDTO(
                LanguageEnum.EN_US,
                ThemeEnum.DARK,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                10,
                ContentFilterLevelEnum.MODERATE,
                FontSizeScaleEnum.SMALL,
                "UTC"
        );

        doNothing().when(mapper).merge(eq(dto), eq(settings));

        when(repository.save(any(UserSettingsModel.class))).thenReturn(settings);

        UserSettingsModel result = this.service.update(dto, settings);

        assertThat(result).isNotNull();

        InOrder order = inOrder(mapper, repository);
        order.verify(mapper).merge(dto, settings);
        order.verify(repository).save(settings);

        verifyNoMoreInteractions(mapper, repository);
    }

}
