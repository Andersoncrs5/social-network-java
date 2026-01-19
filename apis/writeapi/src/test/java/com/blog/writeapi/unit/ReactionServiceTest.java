package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.repositories.ReactionRepository;
import com.blog.writeapi.services.providers.ReactionService;
import com.blog.writeapi.utils.mappers.ReactionMapper;
import org.junit.jupiter.api.DisplayName;
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
public class ReactionServiceTest {

    @Mock private ReactionRepository repository;
    @Mock private Snowflake generator;
    @Mock private ReactionMapper mapper;

    @InjectMocks private ReactionService service;

    ReactionModel reaction = new ReactionModel().toBuilder()
            .id(1998780200074176609L)
            .name("HEART")
            .emojiUrl("https://www.shutterstock.com/shutterstock/photos/2276851457/display_1500/stock-vector-beating-heart-emoji-isolated-on-white-background-emoticons-symbol-modern-simple-vector-printed-2276851457.jpg")
            .emojiUnicode("4536465")
            .displayOrder(1L)
            .active(true)
            .visible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    // METHOD: getByIdSimple
    @Test
    public void shouldGetReactionById() {
        when(this.repository.findById(this.reaction.getId())).thenReturn(Optional.of(this.reaction));

        ReactionModel model = this.service.getByIdSimple(this.reaction.getId());

        assertThat(model).isEqualTo(this.reaction);

        verify(repository, times(1)).findById(this.reaction.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return true when exec METHOD: existsByName")
    public void shouldExistsReactionByNameReturnTrue() {
        when(repository.existsByNameIgnoreCase(this.reaction.getName())).thenReturn(true);

        Boolean exists = this.service.existsByName(this.reaction.getName());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByNameIgnoreCase(this.reaction.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return false when exec METHOD: existsByName")
    public void shouldExistsReactionByNameReturnFalse() {
        when(repository.existsByNameIgnoreCase(this.reaction.getName())).thenReturn(false);

        Boolean exists = this.service.existsByName(this.reaction.getName());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByNameIgnoreCase(this.reaction.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return true when exec METHOD: EmojiUnicode")
    public void shouldExistsReactionByEmojiUnicodeReturnTrue() {
        when(repository.existsByEmojiUnicodeIgnoreCase(this.reaction.getEmojiUnicode())).thenReturn(true);

        Boolean exists = this.service.existsByEmojiUnicode(this.reaction.getEmojiUnicode());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByEmojiUnicodeIgnoreCase(this.reaction.getEmojiUnicode());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return false when exec METHOD: existsByEmojiUnicode")
    public void shouldExistsReactionByEmojiUnicodeReturnFalse() {
        when(repository.existsByEmojiUnicodeIgnoreCase(this.reaction.getEmojiUnicode())).thenReturn(false);

        Boolean exists = this.service.existsByEmojiUnicode(this.reaction.getEmojiUnicode());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByEmojiUnicodeIgnoreCase(this.reaction.getEmojiUnicode());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return true when exec METHOD: existsById")
    public void shouldExistsReactionByIdReturnTrue() {
        when(repository.existsById(this.reaction.getId())).thenReturn(true);

        Boolean exists = this.service.existsById(this.reaction.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(this.reaction.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test if return false when exec METHOD: existsById")
    public void shouldExistsReactionByIdReturnFalse() {
        when(repository.existsById(this.reaction.getId())).thenReturn(false);

        Boolean exists = this.service.existsById(this.reaction.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(this.reaction.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should to delete reaction when exec METHOD: delete")
    public void shouldDeleteReaction() {
        doNothing().when(this.repository).delete(this.reaction);

        this.service.delete(this.reaction);

        verify(repository, times(1)).delete(this.reaction);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should create new reaction when exec METHOD: create")
    public void shouldCreateReaction() {
        CreateReactionDTO dto = new CreateReactionDTO(
                this.reaction.getName(),
                this.reaction.getEmojiUrl(),
                this.reaction.getEmojiUnicode(),
                this.reaction.getDisplayOrder(),
                this.reaction.isActive(),
                this.reaction.isVisible(),
                this.reaction.getType()
        );

        when(this.mapper.toModel(dto)).thenReturn(this.reaction);
        when(this.generator.nextId()).thenReturn(this.reaction.getId());
        when(repository.save(this.reaction)).thenReturn(this.reaction);

        ReactionModel model = this.service.create(dto);

        assertThat(model).isEqualTo(this.reaction);

        verify(this.mapper, times(1)).toModel(dto);
        verify(this.generator, times(1)).nextId();
        verify(this.repository, times(1)).save(any(ReactionModel.class));

        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);
        verifyNoMoreInteractions(mapper);

        InOrder inOrder = inOrder(this.mapper, this.generator, this.repository);

        inOrder.verify(this.mapper).toModel(dto);
        inOrder.verify(this.generator).nextId();
        inOrder.verify(this.repository).save(any(ReactionModel.class));

        inOrder.verifyNoMoreInteractions();
    }

}
