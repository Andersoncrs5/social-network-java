package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;
import com.blog.writeapi.repositories.UserProfileRepository;
import com.blog.writeapi.services.providers.UserProfileService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository repository;

    @Mock
    private Snowflake generator;

    @InjectMocks
    private UserProfileService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .createdAt(OffsetDateTime.now())
            .build();

    UserProfileModel profile = new UserProfileModel().toBuilder()
            .id(1998780200074111609L)
            .bio("AnyBio")
            .avatarUrl("https://github.com/Andersoncrs5")
            .websiteUrls(Set.of("https://github.com/Andersoncrs5/social-network-java", "https://github.com/Andersoncrs5/SocialNetwork"))
            .visibility(ProfileVisibilityEnum.PUBLIC)
            .user(this.user)
            .build();

    @Test
    void shouldReturnTrueWhenExistsByUser() {
        when(repository.existsByUser(this.user)).thenReturn(true);

        Boolean exists = this.service.existsByUser(this.user);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUser(this.user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUser() {
        when(repository.existsByUser(this.user)).thenReturn(false);

        Boolean exists = this.service.existsByUser(this.user);

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUser(this.user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnUserProfileWhenGetByUser() {
        when(repository.findByUser(user)).thenReturn(Optional.of(this.profile));

        Optional<UserProfileModel> byUser = this.service.getByUser(this.user);

        assertThat(byUser.isPresent()).isTrue();

        verify(repository, times(1)).findByUser(this.user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetByUser() {
        when(repository.findByUser(user)).thenReturn(Optional.empty());

        Optional<UserProfileModel> byUser = this.service.getByUser(this.user);

        assertThat(byUser.isEmpty()).isTrue();

        verify(repository, times(1)).findByUser(this.user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(this.profile);

        this.service.delete(this.profile);

        verify(repository, times(1)).delete(this.profile);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewProfile() {
        when(repository.save(any(UserProfileModel.class))).thenAnswer(i -> i.getArgument(0));
        when(generator.nextId()).thenReturn(this.profile.getId());

        UserProfileModel model = this.service.create(user);

        assertThat(model.getId()).isEqualTo(this.profile.getId());

        verify(repository, times(1)).save(any(UserProfileModel.class));
        verify(generator, times(1)).nextId();

        verifyNoMoreInteractions(repository, generator);

        InOrder inOrder = inOrder(generator, repository);

        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(any(UserProfileModel.class));

    }

}
