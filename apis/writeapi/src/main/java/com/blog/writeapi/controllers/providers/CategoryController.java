package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.CategoryControllerDocs;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.services.providers.CategoryService;
import com.blog.writeapi.utils.mappers.CategoryMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService service;
    private final CategoryMapper mapper;

    @Override
    public ResponseEntity<?> create(@Valid @RequestBody CreateCategoryDTO dto, HttpServletRequest request) {
        CategoryModel parent = null;

        if (dto.parentId() != null) {

            Optional<CategoryModel> optional = this.service.getById(dto.parentId());

            if (optional.isEmpty()) {
                ResponseHttp<Object> res = new ResponseHttp<>(
                        null,
                        "Category not exists",
                        UUID.randomUUID().toString(),
                        1,
                        false,
                        OffsetDateTime.now()
                );

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            parent = optional.get();
        }

        CategoryModel category = this.service.create(dto, parent);

        CategoryDTO categoryDto = this.mapper.toDTO(category);

        ResponseHttp<CategoryDTO> res = new ResponseHttp<>(
                categoryDto,
                "Category created with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }



}
