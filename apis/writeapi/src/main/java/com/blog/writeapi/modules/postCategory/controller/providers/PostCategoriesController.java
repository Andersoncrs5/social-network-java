package com.blog.writeapi.modules.postCategory.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.modules.postCategory.controller.docs.PostCategoriesControllerDocs;
import com.blog.writeapi.modules.postCategory.dtos.CreatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.PostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.UpdatePostCategoriesDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.postCategory.models.PostCategoriesModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.category.service.docs.ICategoryService;
import com.blog.writeapi.modules.postCategory.service.docs.IPostCategoriesService;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.utils.annotations.validations.PostCategories.isPostCategoryAuthor.IsPostCategoryAuthor;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.PostCategoriesMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/post-category")
@RequiredArgsConstructor
@Validated
public class PostCategoriesController implements PostCategoriesControllerDocs {

    private final IPostCategoriesService service;
    private final IPostService postService;
    private final ICategoryService categoryService;
    private final PostCategoriesMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> create(
            @Valid @RequestBody CreatePostCategoriesDTO dto,
            HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostModel post = this.postService.getByIdSimple(dto.postId());
        CategoryModel category = this.categoryService.getByIdSimple(dto.categoryId());

        Boolean exists = this.service.existsByPostAndCategory(post, category);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseHttp<>(
                        null,
                        "Category already was added!",
                        idempotencyKey,
                        1,
                        false,
                        OffsetDateTime.now()
                )
            );
        }

        PostCategoriesModel categoriesModel = this.service.create(dto, post, category);

        PostCategoriesDTO mapperDTO = this.mapper.toDTO(categoriesModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseHttp<>(
                mapperDTO,
                "Category added with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
            )
        );
    }

    @Override
    @IsPostCategoryAuthor
    public ResponseEntity<?> del(
            @PathVariable @IsId Long id, HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        this.service.delete(postCategoriesModel);

        return ResponseEntity.status(HttpStatus.OK).body(
            new ResponseHttp<>(
                null,
                "Category removed with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
            )
        );
    }

    @Override
    public ResponseEntity<?> get(
            @PathVariable @IsId Long id, HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        PostCategoriesDTO dto = this.mapper.toDTO(postCategoriesModel);

        ResponseHttp<PostCategoriesDTO> res = new ResponseHttp<>(
                dto,
                "Resource found with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    @IsPostCategoryAuthor
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdatePostCategoriesDTO dto,
            HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        PostCategoriesModel update = this.service.updatev2(dto, postCategoriesModel);

        PostCategoriesDTO mapperDTO = this.mapper.toDTO(update);

        ResponseHttp<PostCategoriesDTO> res = new ResponseHttp<>(
            mapperDTO,
            "Resource updated with successfully",
            idempotencyKey,
            1,
            true,
            OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
