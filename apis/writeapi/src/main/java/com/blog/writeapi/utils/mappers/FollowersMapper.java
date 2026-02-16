package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.modules.followers.dtos.FollowersDTO;
import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FollowersMapper {

    FollowersDTO toDTO(@IsModelInitialized FollowersModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "follower", ignore = true)
    @Mapping(target = "following", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void merge(UpdateFollowersDTO dto, @MappingTarget FollowersModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
