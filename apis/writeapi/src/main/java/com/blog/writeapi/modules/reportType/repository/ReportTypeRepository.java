package com.blog.writeapi.modules.reportType.repository;

import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTypeRepository extends JpaRepository<ReportTypeModel, Long> {
    Boolean existsByName(@NotBlank String name);
}
