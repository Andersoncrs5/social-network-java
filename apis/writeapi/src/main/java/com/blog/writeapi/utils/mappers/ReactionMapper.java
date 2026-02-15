package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.UpdateReactionDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    ReactionModel toModel(ReactionDTO dto);

    ReactionDTO toDTO(ReactionModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReactionModel toModel(CreateReactionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void merge(UpdateReactionDTO dto, @MappingTarget ReactionModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
