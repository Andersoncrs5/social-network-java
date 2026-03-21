package com.blog.writeapi.modules.commentView.service.interfaces;

import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.commentView.model.CommentViewModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.time.LocalDate;

public interface ICommentViewService {
    void delete(
            @IsModelInitialized CommentViewModel view
    );
    boolean existsByUserAndCommentAndViewedAtDate(
            @IsId Long userId,
            @IsId Long commentId,
            LocalDate viewedAtDate
    );
    CommentViewModel create(
            @IsId Long userId,
            @IsId Long commentId,
            ClientMetadataDTO metadata,
            LocalDate today
    );
    void recordView(
            @IsId Long userId,
            @IsId Long commentId,
            ClientMetadataDTO metadata
    );
}
