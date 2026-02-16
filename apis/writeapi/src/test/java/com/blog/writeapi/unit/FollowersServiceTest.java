package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.modules.followers.repository.FollowersRepository;
import com.blog.writeapi.modules.followers.service.providers.FollowersService;
import com.blog.writeapi.modules.user.models.UserModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowersServiceTest {

    @Mock
    private Snowflake generator;
    @Mock
    private FollowersRepository repository;
    @InjectMocks
    private FollowersService service;

    UserModel follower = new UserModel().toBuilder()
            .id(1111111110011111111L)
            .name("user A")
            .email("usera@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserModel followering = new UserModel().toBuilder()
            .id(1998780200011111111L)
            .name("user B")
            .email("userb@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    FollowersModel model = new FollowersModel().toBuilder()
            .id(1998780200022222222L)
            .follower(follower)
            .following(followering)
            .isMuted(true)
            .notifyPosts(true)
            .notifyComments(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnFollowersWhenGetByFollowerAndFollowing() {
        when(repository.findByFollowerAndFollowing(follower, followering))
                .thenReturn(Optional.of(this.model));

        Optional<FollowersModel> optional = this.service.getByFollowerAndFollowing(follower, followering);

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(model.getId());

        verify(repository, times(1)).findByFollowerAndFollowing(follower, followering);
    }

    @Test
    void shouldReturnNullWhenGetByFollowerAndFollowing() {
        when(repository.findByFollowerAndFollowing(follower, followering))
                .thenReturn(Optional.empty());

        Optional<FollowersModel> optional = this.service.getByFollowerAndFollowing(follower, followering);

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByFollowerAndFollowing(follower, followering);
    }

    @Test
    void shouldDeleteFollow() {
        doNothing().when(repository).delete(model);

        this.service.delete(model);

        verify(this.repository, times(1)).delete(model);
    }

    @Test
    void shouldCreateFollow() {
        when(generator.nextId()).thenReturn(model.getId());
        when(repository.save(any(FollowersModel.class)))
                .thenReturn(model);

        FollowersModel follow = this.service.create(model.getFollower(), model.getFollowing());

        assertThat(follow.getId()).isEqualTo(model.getId());

        verify(repository, times(1)).save(any(FollowersModel.class));
        verify(generator, times(1)).nextId();

        InOrder order = inOrder(repository, generator);

        order.verify(generator).nextId();
        order.verify(repository).save(any(FollowersModel.class));
    }

}
