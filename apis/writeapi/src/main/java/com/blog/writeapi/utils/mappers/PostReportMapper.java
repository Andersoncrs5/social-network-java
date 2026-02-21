package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.PostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        uses = {
                PostMapper.class,
                UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostReportMapper {

    PostReportModel toModel(PostReportDTO dto);

    PostReportDTO toDTO(@IsModelInitialized PostReportModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostReportModel toModel(CreatePostReportDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "postAuthorId", ignore = true)
    @Mapping(target = "postContentSnapshot", ignore = true)
    @Mapping(target = "aiToxicityScore", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void merge(UpdatePostReportDTO dto, @MappingTarget @IsModelInitialized PostReportModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
