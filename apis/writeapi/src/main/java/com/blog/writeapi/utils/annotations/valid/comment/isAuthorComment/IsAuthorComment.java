package com.blog.writeapi.utils.annotations.valid.comment.isAuthorComment;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@commentSecurity.isAuthor(#id, T(com.blog.writeapi.utils.security.SecurityUtils).getUserEmail())")
public @interface IsAuthorComment {
}
