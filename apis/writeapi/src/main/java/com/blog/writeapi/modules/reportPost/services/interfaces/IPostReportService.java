package com.blog.writeapi.modules.reportPost.services.interfaces;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

public interface IPostReportService {
    PostReportModel findByIdSimple(@IsId Long id);
    Boolean existsByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    PostReportModel create(
            CreatePostReportDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report
    );
    PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized UserModel moderator
    );
    void delete(@IsModelInitialized PostReportModel report);
}
