package com.blog.writeapi.modules.userReport.repository;

import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReportModel, Long> {

    boolean existsByReportedUserIdAndReporterId(
            @IsId Long reportedUserId,
            @IsId Long reporterId
    );
}
