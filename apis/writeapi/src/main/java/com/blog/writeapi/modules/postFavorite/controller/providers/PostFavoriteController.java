package com.blog.writeapi.modules.postFavorite.controller.providers;

import com.blog.writeapi.modules.postFavorite.controller.docs.PostFavoriteControllerDocs;
import com.blog.writeapi.modules.postFavorite.dtos.PostFavoriteDTO;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.postFavorite.service.docs.IPostFavoriteService;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.PostFavoriteMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-favorite")
public class PostFavoriteController implements PostFavoriteControllerDocs {

    private final IPostFavoriteService service;
    private final IUserService userService;
    private final IPostService iPostService;
    private final ITokenService iTokenService;
    private final PostFavoriteMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long postId,
            HttpServletRequest request
            ) {
        Long userID = this.iTokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userID);
        PostModel post = this.iPostService.getByIdSimple(postId);

        Optional<PostFavoriteModel> favor = this.service.getByPostAndUser(post, user);

        if (favor.isPresent()) {
            this.service.delete(favor.get());

            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "Post removed the favorites with successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.OK).body(res);
        }

        PostFavoriteModel model = this.service.create(post, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseHttp<>(
                        this.mapper.toDTO(model),
                        "Post added with favorites with successfully",
                        UUID.randomUUID().toString(),
                        1,
                        true,
                        OffsetDateTime.now()
                )
        );
    }


}
