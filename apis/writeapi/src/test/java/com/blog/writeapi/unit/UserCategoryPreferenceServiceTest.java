package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserCategoryPreferenceRepository;
import com.blog.writeapi.services.providers.UserCategoryPreferenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCategoryPreferenceServiceTest {

    @Mock private UserCategoryPreferenceRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks private UserCategoryPreferenceService service;

    CategoryModel category = new CategoryModel().toBuilder()
            .id(1111110200074176609L)
            .name("TI")
            .description("Any Desc")
            .slug("ti")
            .isActive(true)
            .visible(true)
            .displayOrder(1)
            .version(1L)
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

    UserCategoryPreferenceModel preference = new UserCategoryPreferenceModel().toBuilder()
            .id(1998780200071111111L)
            .user(user)
            .category(category)
            .interestScore(37.9)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnUserCategoryPreferenceWhenGetByUserAndCategory() {
        when(repository.findByUserAndCategory(user, category))
                .thenReturn(Optional.of(preference));

        Optional<UserCategoryPreferenceModel> optional = this.service.getByUserAndCategory(user, category);

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findByUserAndCategory(user, category);
    }

    @Test
    void shouldReturnNullWhenGetByUserAndCategory() {
        when(repository.findByUserAndCategory(user, category))
                .thenReturn(Optional.empty());

        Optional<UserCategoryPreferenceModel> optional = this.service.getByUserAndCategory(user, category);

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByUserAndCategory(user, category);
    }

    @Test
    void shouldDeleteUserCategoryPreference() {
        doNothing().when(repository).delete(preference);

        this.service.delete(preference);

        verify(repository, times(1)).delete(preference);
    }

    @Test
    void shouldCreatePreference() {
        when(this.generator.nextId())
                .thenReturn(this.preference.getId());
        when(repository.save(any(UserCategoryPreferenceModel.class)))
                .thenReturn(this.preference);

        UserCategoryPreferenceModel model = service.create(user, category);
        assertThat(model.getId()).isEqualTo(preference.getId());

        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any(UserCategoryPreferenceModel.class));
    }

}
