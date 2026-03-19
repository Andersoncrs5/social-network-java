package com.blog.writeapi.modules.commentReport.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<@NonNull CommentReportModel, @NonNull Long> {
    Optional<CommentReportModel> findByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    Boolean existsByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
}
