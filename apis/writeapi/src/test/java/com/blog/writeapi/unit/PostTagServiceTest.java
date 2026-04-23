package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postTag.models.PostTagModel;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.modules.postTag.repository.PostTagRepository;
import com.blog.writeapi.modules.postTag.service.providers.PostTagService;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.PostTagMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostTagServiceTest {

    @Mock private PostTagRepository repository;
    @Mock private Snowflake generator;
    @Mock private PostTagMapper mapper;

    @InjectMocks private PostTagService service;

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

    PostTagModel postTag = new PostTagModel().toBuilder()
            .id(1998780211074176609L)
            .post(this.post)
            .tag(this.tag)
            .active(true)
            .visible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    @DisplayName("Should delete post tag successfully when ID exists")
    public void shouldDeletePostTagSuccessfully() {
        Long id = postTag.getId();
        when(repository.deleteAndCount(id)).thenReturn(1);

        service.deleteByID(id);

        verify(repository, times(1)).deleteAndCount(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw ModelNotFoundException when ID does not exist")
    public void shouldThrowExceptionWhenIdDoesNotExist() {
        Long nonExistentId = 999L;
        when(repository.deleteAndCount(nonExistentId)).thenReturn(0);

        assertThatThrownBy(() -> service.deleteByID(nonExistentId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Post tag not found");

        verify(repository, times(1)).deleteAndCount(nonExistentId);
        verifyNoMoreInteractions(repository);
    }

    // METHOD: existsByPostAndTag
    @Test
    void shouldReturnTrueWhenExistsByPostAndTag() {
        when(repository.existsByPostIdAndTagId(this.post.getId(), this.tag.getId())).thenReturn(true);

        Boolean exists = this.service.existsByPostAndTag(this.post.getId(), this.tag.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByPostIdAndTagId(this.post.getId(), this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByPostAndTag() {
        when(repository.existsByPostIdAndTagId(this.post.getId(), this.tag.getId())).thenReturn(false);

        Boolean exists = this.service.existsByPostAndTag(this.post.getId(), this.tag.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByPostIdAndTagId(this.post.getId(), this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    // METHOD: getById
    @Test
    void shouldReturnPostTagWhenGetById() {
        when(repository.findById(this.postTag.getId())).thenReturn(Optional.of(this.postTag));

        Optional<PostTagModel> optional = this.service.getById(this.postTag.getId());

        assertThat(optional.isEmpty()).isFalse();

        verify(repository, times(1)).findById(this.postTag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetById() {
        when(repository.findById(this.postTag.getId())).thenReturn(Optional.empty());

        Optional<PostTagModel> optional = this.service.getById(this.postTag.getId());

        assertThat(optional.isPresent()).isFalse();

        verify(repository, times(1)).findById(this.postTag.getId());
        verifyNoMoreInteractions(repository);
    }

    // METHOD: delete
    @Test
    void shouldRemoveTagOfPost() {
        doNothing().when(repository).delete(this.postTag);

        this.service.delete(this.postTag);

        verify(repository, times(1)).delete(this.postTag);
        verifyNoMoreInteractions(repository);
    }

    // METHOD: create
    @Test
    void shouldAddedTagToPost() {
        CreatePostTagDTO dto = new CreatePostTagDTO(
                post.getId(),
                tag.getId(),
                this.postTag.isActive(),
                this.postTag.isVisible()
        );

        PostTagModel modelToReturn = new PostTagModel();

        when(mapper.toModel(dto)).thenReturn(modelToReturn);
        when(generator.nextId()).thenReturn(this.postTag.getId());

        when(repository.save(any(PostTagModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostTagModel result = this.service.create(dto, post, tag, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(this.postTag.getId());
        assertThat(result.getPost()).isEqualTo(post);
        assertThat(result.getTag()).isEqualTo(tag);

        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any(PostTagModel.class));
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenTagIsInactive() {
        CreatePostTagDTO dto = new CreatePostTagDTO(post.getId(), tag.getId(), true, true);
        TagModel inactiveTag = tag.toBuilder().isActive(false).build();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                service.create(dto, post, inactiveTag, user.getId())
        );

        assertThat(exception.getMessage()).isEqualTo("Tag is inactive");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);

        verifyNoInteractions(repository, generator, mapper);
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenUserIsNotAuthor() {
        CreatePostTagDTO dto = new CreatePostTagDTO(post.getId(), tag.getId(), true, true);
        Long otherUserId = 9999L;

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                service.create(dto, post, tag, otherUserId)
        );

        assertThat(exception.getMessage()).isEqualTo("You are not the author of this post");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);

        verifyNoInteractions(repository, generator, mapper);
    }

    @Test
    void shouldThrowUniqueConstraintViolationWhenPostTagAlreadyExists() {
        CreatePostTagDTO dto = new CreatePostTagDTO(post.getId(), tag.getId(), true, true);
        when(mapper.toModel(dto)).thenReturn(new PostTagModel());
        when(generator.nextId()).thenReturn(postTag.getId());

        var rootCause = new RuntimeException("uk_post_tag");
        when(repository.save(any(PostTagModel.class)))
                .thenThrow(new DataIntegrityViolationException("Conflict", rootCause));

        assertThrows(UniqueConstraintViolationException.class, () ->
                service.create(dto, post, tag, user.getId())
        );

        verify(repository, times(1)).save(any(PostTagModel.class));
    }

    @Test
    void shouldThrowInternalServerErrorWhenGenericErrorOccurs() {
        CreatePostTagDTO dto = new CreatePostTagDTO(post.getId(), tag.getId(), true, true);
        when(mapper.toModel(dto)).thenReturn(new PostTagModel());
        when(generator.nextId()).thenReturn(postTag.getId());
        when(repository.save(any(PostTagModel.class)))
                .thenThrow(new RuntimeException("Unexpected DB error"));

        assertThrows(InternalServerErrorException.class, () ->
                service.create(dto, post, tag, user.getId())
        );
    }

}
