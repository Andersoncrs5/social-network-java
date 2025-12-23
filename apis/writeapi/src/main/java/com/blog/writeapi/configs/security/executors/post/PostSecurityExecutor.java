package com.blog.writeapi.configs.security.executors.post;

import com.blog.writeapi.services.interfaces.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("postSecurity")
public class PostSecurityExecutor {

    @Autowired
    private IPostService postService;

    public boolean isAuthor(Long postId, String email) {
        if (postId == null || email == null) return false;

        return postService.getById(postId)
                .map(post -> post.getAuthor().getEmail().equals(email))
                .orElse(false);
    }
}
