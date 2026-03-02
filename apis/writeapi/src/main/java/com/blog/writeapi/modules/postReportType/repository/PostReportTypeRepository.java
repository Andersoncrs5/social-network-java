package com.blog.writeapi.modules.postReportType.repository;

import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportTypeRepository extends JpaRepository<PostReportTypeModel, Long> {
    Optional<PostReportTypeModel> findByReportAndType(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized PostReportTypeModel type
    );
}
