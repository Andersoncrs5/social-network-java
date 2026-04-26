package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postVote.dtos.TogglePostVoteDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postVote.gateway.PostVoteGatewayModule;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;
import com.blog.writeapi.modules.postVote.repository.PostVoteRepository;
import com.blog.writeapi.modules.postVote.service.providers.PostVoteService;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostVoteServiceTest {

    @Mock private PostVoteGatewayModule gateway;
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
    void shouldThrowBusinessRuleExceptionWhenAuthorIsBlocked() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(post.getId(), VoteTypeEnum.UPVOTE);

        UserModel author = new UserModel().toBuilder().id(999L).build();
        PostModel postWithAuthor = post.toBuilder().author(author).build();

        when(gateway.isBlocked(user.getId(), author.getId())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> service.create(dto, user, postWithAuthor));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionWhenAlreadyVoted() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(post.getId(), VoteTypeEnum.UPVOTE);

        when(generator.nextId()).thenReturn(vote.getId());

        var rootCause = new RuntimeException("uk_post_vote");
        var dataIntegrityException = new DataIntegrityViolationException("Conflict", rootCause);
        when(repository.save(any(PostVoteModel.class))).thenThrow(dataIntegrityException);

        assertThrows(UniqueConstraintViolationException.class, () -> service.create(dto, user, post));

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldThrowInternalServerErrorExceptionWhenDatabaseFails() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(post.getId(), VoteTypeEnum.UPVOTE);
        when(generator.nextId()).thenReturn(vote.getId());
        when(repository.save(any(PostVoteModel.class))).thenThrow(new RuntimeException("DB Down"));

        assertThrows(InternalServerErrorException.class, () -> service.create(dto, user, post));
    }

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
    void shouldDeleteVoteAndHandleUpvoteDecrementMetric() {
        PostVoteModel upvote = this.vote.toBuilder().type(VoteTypeEnum.UPVOTE).build();

        this.service.delete(upvote);

        verify(repository).delete(upvote);
        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(upvote.getPost().getId()) &&
                        metric.metric() == PostMetricEnum.UPVOTE &&
                        metric.action() == ActionEnum.RED
        ));
    }

    @Test
    void shouldDeleteVoteAndHandleDownvoteDecrementMetric() {
        PostVoteModel downvote = this.vote.toBuilder().type(VoteTypeEnum.DOWNVOTE).build();

        this.service.delete(downvote);

        verify(repository).delete(downvote);
        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(downvote.getPost().getId()) &&
                        metric.metric() == PostMetricEnum.DOWNVOTE &&
                        metric.action() == ActionEnum.RED
        ));
    }

    @Test
    void shouldCreateNewVoteAndHandleUpvoteMetric() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(this.post.getId(), VoteTypeEnum.UPVOTE);
        PostVoteModel voteUpvote = this.vote.toBuilder().type(VoteTypeEnum.UPVOTE).build();

        when(generator.nextId()).thenReturn(voteUpvote.getId());
        when(repository.save(any())).thenReturn(voteUpvote);

        this.service.create(dto, user, post);

        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(post.getId()) &&
                        metric.metric() == PostMetricEnum.UPVOTE &&
                        metric.action() == ActionEnum.SUM
        ));
    }

    @Test
    void shouldCreateNewVoteAndHandleDownvoteMetric() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(this.post.getId(), VoteTypeEnum.DOWNVOTE);
        PostVoteModel voteDownvote = this.vote.toBuilder().type(VoteTypeEnum.DOWNVOTE).build();

        when(generator.nextId()).thenReturn(voteDownvote.getId());
        when(repository.save(any())).thenReturn(voteDownvote);

        this.service.create(dto, user, post);

        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(post.getId()) &&
                        metric.metric() == PostMetricEnum.DOWNVOTE &&
                        metric.action() == ActionEnum.SUM
        ));
    }

    @Test
    void shouldAllowVotingWhenUserIsNotBlocked() {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(post.getId(), VoteTypeEnum.UPVOTE);
        UserModel author = new UserModel().toBuilder().id(888L).build();
        PostModel postWithAuthor = post.toBuilder().author(author).build();

        when(gateway.isBlocked(user.getId(), author.getId())).thenReturn(false);
        when(generator.nextId()).thenReturn(vote.getId());
        when(repository.save(any())).thenReturn(vote);

        assertDoesNotThrow(() -> service.create(dto, user, postWithAuthor));

        verify(repository).save(any());
        verify(gateway).handleMetric(any());
    }

}
