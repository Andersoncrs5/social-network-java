package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UserReportDTO;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {
                UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserReportMapper {

    UserReportDTO toDTO(@IsModelInitialized UserReportModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reportedUser", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "moderatedAt", ignore = true)
    @Mapping(target = "userProfileSnapshot", ignore = true)
    @Mapping(target = "aiTrustScoreAtReport", ignore = true)
    // @Mapping(target = "reportTypes", ignore = true)
    UserReportModel toModel(CreateUserReportDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportedUser", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "userProfileSnapshot", ignore = true)
    @Mapping(target = "aiTrustScoreAtReport", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // @Mapping(target = "reportTypes", ignore = true)
    void merge(UpdateUserReportDTO dto, @MappingTarget @IsModelInitialized UserReportModel model);

}
