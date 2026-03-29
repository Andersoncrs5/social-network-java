package com.blog.writeapi.modules.userReportType.service.interfaces;

import com.blog.writeapi.modules.userReportType.model.UserReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

import java.util.Optional;

public interface IUserReportTypeService {
    ResultToggle<UserReportTypeModel> toggle(
            @IsId Long reportId,
            @IsId Long typeId,
            @IsId Long userId
    );
    UserReportTypeModel create(
            @IsId Long reportId,
            @IsId Long typeId,
            @IsId Long userId
    );
    void delete(@IsModelInitialized UserReportTypeModel model);
    Optional<UserReportTypeModel> findByReportIdAndTypeId(
            @IsId Long reportId,
            @IsId Long typeId
    );
    boolean existsByReportIdAndTypeId(
            @IsId Long reportId,
            @IsId Long typeId
    );
}
