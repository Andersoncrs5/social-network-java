package com.blog.writeapi.modules.postShare.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.postShare.controller.docs.IPostShareControllerDocs;
import com.blog.writeapi.modules.postShare.dto.CreatePostShareDTO;
import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.modules.postShare.service.interfaces.IPostShareService;
import com.blog.writeapi.utils.mappers.PostShareMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/post-share")
public class PostShareController implements IPostShareControllerDocs {

    private final IPostShareService service;
    private final PostShareMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreatePostShareDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        boolean exists = this.service.existsByUserIdAndPostIdAndPlatform(principal.getId(), dto.postId(), dto.platform());

        if (exists) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    new ResponseHttp<>(
                        null,
                        "Post already shared!",
                        UUID.randomUUID().toString(),
                        1,
                        false,
                        OffsetDateTime.now()
                    )
                );
        }

        PostShareModel model = this.service.create(principal.getId(), dto.postId(), dto.platform());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                new ResponseHttp<>(
                    this.mapper.toDTO(model),
                    "Post shared!",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

}
