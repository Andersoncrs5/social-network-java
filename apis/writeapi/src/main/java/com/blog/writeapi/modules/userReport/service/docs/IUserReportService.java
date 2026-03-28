package com.blog.writeapi.modules.userReport.service.docs;

import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

public interface IUserReportService {
    UserReportModel findByIdSimple(@IsId Long id);
    void delete(
            @IsModelInitialized UserReportModel report,
            @IsId Long userId
    );
    boolean existsByReportedUserIdAndReporterId(
            @IsId Long reportedUserId,
            @IsId Long reporterId
    );
    UserReportModel create(
            CreateUserReportDTO dto,
            @IsId Long reportedUserId,
            @IsId Long reporterId
    );
    UserReportModel update(
            UpdateUserReportDTO dto,
            @IsModelInitialized UserReportModel report,
            @IsId Long moderatorId
    );
}
