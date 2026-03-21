package com.blog.writeapi.modules.commentView.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentView.model.CommentViewModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CommentViewRepository extends JpaRepository<CommentViewModel, Long> {
    boolean existsByUserAndCommentAndViewedAtDate(UserModel user, CommentModel post, LocalDate viewedAtDate);
    boolean existsByUserIdAndCommentIdAndViewedAtDate(
            @IsId Long userId,
            @IsId Long commentId,
            LocalDate viewedAtDate
    );

}
