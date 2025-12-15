package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.CategoryControllerDocs;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.services.providers.CategoryService;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService service;

    public ResponseEntity<?> create(@Valid @RequestBody CreateCategoryDTO dto, HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

}
