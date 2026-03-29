package com.blog.writeapi.modules.userReportType.repository;

import com.blog.writeapi.modules.userReportType.model.UserReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReportTypeRepository extends JpaRepository<UserReportTypeModel, Long> {
    boolean existsByReportIdAndTypeId(@IsId Long reportId, @IsId Long typeId);

    Optional<UserReportTypeModel> findByReportIdAndTypeId(Long reportId, Long typeId);
}
