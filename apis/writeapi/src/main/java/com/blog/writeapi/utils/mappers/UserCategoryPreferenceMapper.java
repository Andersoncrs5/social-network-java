package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.userCategoryPreference.UserCategoryPreferenceDTO;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class, UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserCategoryPreferenceMapper {

    UserCategoryPreferenceDTO toDTO(@IsModelInitialized UserCategoryPreferenceModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
