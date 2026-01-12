package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.models.enums.votes.VoteTypeEnum;
import com.blog.writeapi.repositories.PostVoteRepository;
import com.blog.writeapi.services.providers.PostVoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostVoteServiceTest {

    @Mock private PostVoteRepository repository;
    @Mock private Snowflake generator;
    @InjectMocks private PostVoteService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

    PostModel post = new PostModel().toBuilder()
            .id(1998780203274176609L)
            .title("anyTittle")
            .slug("any-title")
            .content("any Content")
            .status(PostStatusEnum.PUBLISHED)
            .readingTime(5)
            .rankingScore(0.0)
            .isFeatured(false)
            .author(user)
            .build();

    PostVoteModel vote = new PostVoteModel().toBuilder()
            .id(1998780203274176909L)
            .user(this.user)
            .post(this.post)
            .type(VoteTypeEnum.UPVOTE)
            .build();

    @Test
    void shouldFindPostVoteByUserAndPost() {
        when(repository.findByUserAndPost(this.user, this.post))
                .thenReturn(Optional.of(this.vote));

        Optional<PostVoteModel> vote = this.service.findByUserAndPost(user, post);

        assertThat(vote.isPresent()).isTrue();
        assertThat(vote.get().getId()).isEqualTo(this.vote.getId());

        verify(repository, times(1)).findByUserAndPost(this.user, this.post);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullFindPostVoteByUserAndPost() {
        when(repository.findByUserAndPost(this.user, this.post))
                .thenReturn(Optional.empty());

        Optional<PostVoteModel> vote = this.service.findByUserAndPost(user, post);

        assertThat(vote.isEmpty()).isTrue();

        verify(repository, times(1)).findByUserAndPost(this.user, this.post);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldVoteByVote() {
        doNothing().when(repository).delete(this.vote);

        this.service.delete(this.vote);

        verify(repository, times(1)).delete(this.vote);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewVote() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                this.post.getId(),
                this.vote.getType()
        );

        when(generator.nextId()).thenReturn(this.vote.getId());
        when(repository.save(any())).thenReturn(this.vote);

        PostVoteModel voteModel = this.service.create(dto, user, post);

        assertThat(voteModel).isEqualTo(vote);

        verify(repository, times(1)).save(any());
        verify(generator, times(1)).nextId();

        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);
    }

}
