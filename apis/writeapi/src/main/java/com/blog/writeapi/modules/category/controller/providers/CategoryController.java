package com.blog.writeapi.modules.category.controller.providers;

import com.blog.writeapi.modules.category.controller.docs.CategoryControllerDocs;
import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.category.dtos.CreateCategoryDTO;
import com.blog.writeapi.modules.category.dtos.UpdateCategoryDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.category.service.docs.ICategoryService;
import com.blog.writeapi.utils.mappers.CategoryMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
@Validated
public class CategoryController implements CategoryControllerDocs {

    private final ICategoryService service;
    private final CategoryMapper mapper;

    @Override
    public ResponseEntity<?> create(@Valid @RequestBody CreateCategoryDTO dto, HttpServletRequest request) {
        CategoryModel parent = null;

        if (dto.parentId() != null) {
            parent = this.service.getByIdSimple(dto.parentId());
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

    @Override
    public ResponseEntity<?> get(
            @PathVariable
            @Positive(message = "The ID provided must be a positive number.")
            Long id,
            HttpServletRequest request
    ) {
        CategoryModel categoryOpt = this.service.getByIdSimple(id);

        CategoryDTO categoryDto = this.mapper.toDTO(categoryOpt);

        ResponseHttp<CategoryDTO> res = new ResponseHttp<>(
                categoryDto,
                "Category found with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable
            @Positive(message = "The ID provided must be a positive number.")
            Long id,
            HttpServletRequest request
    ) {
        CategoryModel categoryOpt = this.service.getByIdSimple(id);

        this.service.delete(categoryOpt);

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "Category deleted with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdateCategoryDTO dto,
            HttpServletRequest request
    ) {

        CategoryModel current = this.service.getByIdSimple(dto.id());

        CategoryModel parent = null;
        if (!Boolean.TRUE.equals(dto.isRoot()) && dto.parentId() != null) {
            parent = this.service.getByIdSimple(dto.parentId());
        }

        CategoryModel updated = this.service.update(dto, current, parent);
        CategoryDTO categoryDTO = this.mapper.toDTO(updated);

        return ResponseEntity.ok(new ResponseHttp<>(
                categoryDTO,
                "Category updated successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }
}
