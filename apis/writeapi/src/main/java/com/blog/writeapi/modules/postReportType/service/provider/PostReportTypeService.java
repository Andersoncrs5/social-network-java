package com.blog.writeapi.modules.postReportType.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.postReportType.repository.PostReportTypeRepository;
import com.blog.writeapi.modules.postReportType.service.interfaces.IPostReportTypeService;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReportTypeService implements IPostReportTypeService {

    private final PostReportTypeRepository repository;
    private final Snowflake generator;

    public Optional<PostReportTypeModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    public void delete(@IsModelInitialized PostReportTypeModel type) {
        this.repository.delete(type);
    }

    public PostReportTypeModel create(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        PostReportTypeModel model = new PostReportTypeModel().toBuilder()
                .id(generator.nextId())
                .report(report)
                .type(type)
                .build();

        return this.repository.save(model);
    }

    public Boolean existsByReportAndType(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        return this.repository.existsByReportAndType(report, type);
    }

    public ResultToggle<PostReportTypeModel> toggle(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        Optional<PostReportTypeModel> exists = this.repository.findByReportAndType(report, type);

        if (exists.isPresent()) {
            this.repository.delete(exists.get());
            return ResultToggle.removed();
        }

        PostReportTypeModel model = PostReportTypeModel.builder()
            .id(generator.nextId())
            .type(type)
            .report(report)
            .build();

        PostReportTypeModel saved = this.repository.save(model);

        return ResultToggle.added(saved);
    }

}
