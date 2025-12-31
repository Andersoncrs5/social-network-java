package com.blog.writeapi.configs.security.executors.comment;

import com.blog.writeapi.services.interfaces.ICommentService;
import com.blog.writeapi.utils.annotations.valid.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
@RequiredArgsConstructor
public class CommentSecurity {

    private final ICommentService service;

    public boolean isAuthor(@IsId Long postId, @EmailConstraint String email) {
        if (postId == null || email == null) return false;

        return service.getByIdSimple(postId)
                .getAuthor()
                .getEmail()
                .equalsIgnoreCase(email);
    }

}
