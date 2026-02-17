package com.blog.writeapi.modules.reportType.services.interfaces;

import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotBlank;

public interface IReportTypeService {
    ReportTypeModel getByIdSimple(@IsId Long id);
    Boolean existsByName(@NotBlank String name);
    void delete(@IsModelInitialized ReportTypeModel type);
    ReportTypeModel create(CreateReportTypeDTO dto);
    ReportTypeModel update(
            @IsModelInitialized ReportTypeModel type,
            UpdateReportTypeDTO dto
    );
}
