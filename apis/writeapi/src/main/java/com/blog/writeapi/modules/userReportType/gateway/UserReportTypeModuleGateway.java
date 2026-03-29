package com.blog.writeapi.modules.userReportType.gateway;

import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.services.interfaces.IReportTypeService;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReport.service.docs.IUserReportService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReportTypeModuleGateway {

    private final IUserReportService userReportService;
    private final IReportTypeService reportTypeService;

    public UserReportModel findUserReportByIdSimple(@IsId Long userReportId) {
        return userReportService.findByIdSimple(userReportId);
    }

    public ReportTypeModel findReportTypeByIdSimple(@IsId Long typeId) {
        return reportTypeService.getByIdSimple(typeId);
    }

}
