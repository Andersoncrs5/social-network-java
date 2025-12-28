package com.blog.writeapi.configs.security.executors.postTag;

import com.blog.writeapi.services.interfaces.IPostTagService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component("postTagSecurity")
@RequiredArgsConstructor
public class PostTagSecurity {

    private final IPostTagService service;

    public boolean isAuthor(@IsId Long id, @NotBlank @Email String email) {
        if (id == null || email == null) return false;

        return service.getByIdSimple(id)
                .getPost()
                .getAuthor()
                .getEmail()
                .equalsIgnoreCase(email);
    }

}