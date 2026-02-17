package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.UpdateReactionDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReportTypeMapper {

    ReportTypeModel toModel(ReportTypeDTO dto);

    ReportTypeDTO toDTO(ReportTypeModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReportTypeModel toModel(CreateReportTypeDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void merge(UpdateReportTypeDTO dto, @MappingTarget ReportTypeModel model);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

}
