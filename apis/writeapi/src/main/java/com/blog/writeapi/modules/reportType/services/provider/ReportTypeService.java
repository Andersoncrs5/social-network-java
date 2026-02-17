package com.blog.writeapi.modules.reportType.services.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.repository.ReportTypeRepository;
import com.blog.writeapi.modules.reportType.services.interfaces.IReportTypeService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.ReportTypeMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReportTypeService implements IReportTypeService {

    private final ReportTypeRepository repository;
    private final Snowflake snowflake;
    private final ReportTypeMapper mapper;

    @Override
    public ReportTypeModel getByIdSimple(@IsId Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ModelNotFoundException("Report type not found")
        );
    }

    @Override
    public Boolean existsByName(@NotBlank String name) {
        return this.repository.existsByName(name);
    }

    @Override
    public void delete(@IsModelInitialized ReportTypeModel type) {
        repository.delete(type);
    }

    @Override
    public ReportTypeModel create(CreateReportTypeDTO dto) {
        ReportTypeModel model = this.mapper.toModel(dto);
        model.setId(this.snowflake.nextId());

        return repository.save(model);
    }

    @Override
    public ReportTypeModel update(
            @IsModelInitialized ReportTypeModel type,
            UpdateReportTypeDTO dto
    ){
        this.mapper.merge(dto, type);

        return this.repository.save(type);
    }

}
