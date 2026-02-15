package com.blog.writeapi.configs.security.executors.postCategories;

import com.blog.writeapi.modules.postCategory.service.docs.IPostCategoriesService;
import com.blog.writeapi.utils.annotations.validations.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("postCategorySecurity")
public class PostCategorySecurityExecutor {

    @Autowired
    private IPostCategoriesService service;

    public boolean isAuthor(@IsId Long id, @EmailConstraint String email) {
        if (id == null || email == null) return false;

        return service.getByIdSimple(id)
                .getPost()
                .getAuthor()
                .getEmail()
                .equalsIgnoreCase(email);
    }

}
