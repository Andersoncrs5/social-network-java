package com.blog.writeapi.configs.security.executors.post;

import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.utils.annotations.validations.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("postSecurity")
@RequiredArgsConstructor
public class PostSecurityExecutor {

    private final IPostService postService;

    public boolean isAuthor(@IsId Long postId, @EmailConstraint String email) {
        if (postId == null || email == null) return false;

        return postService.getByIdSimple(postId)
                .getAuthor()
                .getEmail()
                .equalsIgnoreCase(email);
    }
}
