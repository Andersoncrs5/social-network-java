package com.blog.writeapi.modules.commentReport.service.interfaces;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentReportService {
    CommentReportModel findByIdSimple(@IsId Long id);
    Optional<CommentReportModel> findByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    Boolean existsByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    void delete(@IsModelInitialized CommentReportModel model);
    CommentReportModel create(
            CreateCommentReportDTO dto,
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    CommentReportModel update(
            UpdateCommentReportDTO dto,
            @IsModelInitialized CommentReportModel model,
            @IsModelInitialized UserModel moderator
    );
}
