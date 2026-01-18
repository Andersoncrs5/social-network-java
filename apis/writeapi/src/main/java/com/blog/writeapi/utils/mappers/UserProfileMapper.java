package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.dtos.userProfile.UserProfileDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.models.UserProfileModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserProfileMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void merge(UpdateUserProfileDTO dto, @MappingTarget UserProfileModel category);

    UserProfileDTO toDTO(@NotNull UserProfileModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

}
