package com.blog.writeapi.modules.commentView.gateway;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentViewModuleGateway {

    private final IUserService userService;
    private final IPostService postService;
    private final ICommentService commentService;

    public CommentModel getCommentById(@IsId Long id) {
        return this.commentService.getByIdSimple(id);
    }

    public UserModel getUserById(@IsId Long id) {
        return this.userService.GetByIdSimple(id);
    }

    public PostModel getPostById(@IsId Long id) {
        return this.postService.getByIdSimple(id);
    }

}
