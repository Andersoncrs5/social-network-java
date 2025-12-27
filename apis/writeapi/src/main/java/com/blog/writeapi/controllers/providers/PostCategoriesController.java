package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostCategoriesControllerDocs;
import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.PostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.services.interfaces.ICategoryService;
import com.blog.writeapi.services.interfaces.IPostCategoriesService;
import com.blog.writeapi.services.interfaces.IPostService;
import com.blog.writeapi.utils.annotations.valid.PostCategories.isPostCategoryAuthor.IsPostCategoryAuthor;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.mappers.PostCategoriesMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> create(@Valid @RequestBody CreatePostCategoriesDTO dto, HttpServletRequest request) {
        PostModel post = this.postService.getByIdSimple(dto.postId());
        CategoryModel category = this.categoryService.getByIdSimple(dto.categoryId());

        Boolean exists = this.service.existsByPostAndCategory(post, category);
        if (exists) {
            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "Category already was added!",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }

        PostCategoriesModel categoriesModel = this.service.create(dto, post, category);

        PostCategoriesDTO mapperDTO = this.mapper.toDTO(categoriesModel);

        ResponseHttp<PostCategoriesDTO> res = new ResponseHttp<>(
                mapperDTO,
                "Category added with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Override
    @IsPostCategoryAuthor
    public ResponseEntity<?> del(@PathVariable @IsId Long id, HttpServletRequest request) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        this.service.delete(postCategoriesModel);

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "Category removed with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    public ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        PostCategoriesDTO dto = this.mapper.toDTO(postCategoriesModel);

        ResponseHttp<PostCategoriesDTO> res = new ResponseHttp<>(
                dto,
                "Resource found with successfully",
                UUID.randomUUID().toString(),
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
            HttpServletRequest request
    ) {
        PostCategoriesModel postCategoriesModel = this.service.getByIdSimple(id);

        PostCategoriesModel update = this.service.updatev2(dto, postCategoriesModel);

        PostCategoriesDTO mapperDTO = this.mapper.toDTO(update);

        ResponseHttp<PostCategoriesDTO> res = new ResponseHttp<>(
                mapperDTO,
                "Resource updated with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
