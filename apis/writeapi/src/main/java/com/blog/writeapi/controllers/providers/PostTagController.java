package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostTagControllerDocs;
import com.blog.writeapi.dtos.postTag.CreatePostTagDTO;
import com.blog.writeapi.dtos.postTag.PostTagDTO;
import com.blog.writeapi.dtos.postTag.UpdatePostTagDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostTagModel;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.services.interfaces.*;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.postTag.isAuthorPostTag.IsAuthorPostTag;
import com.blog.writeapi.utils.mappers.PostTagMapper;
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
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-tag")
public class PostTagController implements PostTagControllerDocs {

    private final IPostTagService service;
    private final PostTagMapper mapper;
    private final IPostService postService;
    private final ITagService tagService;
    private final ITokenService tokenService;

    @Override
    public ResponseEntity<?> create(
            @Valid @RequestBody CreatePostTagDTO dto,
            HttpServletRequest request
            ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        PostModel post = this.postService.getByIdSimple(dto.postId());
        TagModel tag = this.tagService.getByIdSimple(dto.tagId());

        if (!post.getAuthor().getId().equals(userId)) {
            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "You are not the author of this post",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        PostTagModel model = this.service.create(dto, post, tag);

        PostTagDTO mapperDTO = this.mapper.toDTO(model);

        ResponseHttp<PostTagDTO> res = new ResponseHttp<>(
                mapperDTO,
                "Tag added with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Override
    @IsAuthorPostTag
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    ) {
        PostTagModel model = this.service.getByIdSimple(id);

        this.service.delete(model);

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "Tag removed with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    @IsAuthorPostTag
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdatePostTagDTO dto,
            HttpServletRequest request
    ) {
        PostTagModel postTag = this.service.getByIdSimple(id);

        PostTagModel update = this.service.update(dto, postTag);

        PostTagDTO mapperDTO = this.mapper.toDTO(update);

        ResponseHttp<PostTagDTO> res = new ResponseHttp<>(
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
