package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.commentReport.dto.CommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {
                CommentMapper.class,
                UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentReportMapper {

    CommentReportModel toModel(CommentReportDTO dto);

    CommentReportDTO toDTO(@IsModelInitialized CommentReportModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "moderatedAt", ignore = true)
    @Mapping(target = "moderatorNotes", ignore = true)
    CommentReportModel toModel(CreateCommentReportDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "commentAuthorId", ignore = true)
    @Mapping(target = "commentContentSnapshot", ignore = true)
    @Mapping(target = "aiToxicityScore", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void merge(UpdateCommentReportDTO dto, @MappingTarget @IsModelInitialized CommentReportModel model);

}
