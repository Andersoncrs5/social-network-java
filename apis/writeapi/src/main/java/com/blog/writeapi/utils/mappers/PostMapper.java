package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.post.UpdatePostDTO;
import com.blog.writeapi.models.PostModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostModel toModel(@NotNull PostDTO dto);
    PostDTO toDTO(@NotNull PostModel post);

    PostModel toModel(@NotNull CreatePostDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(@NotNull UpdatePostDTO dto, @MappingTarget @NotNull PostModel post);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

}
