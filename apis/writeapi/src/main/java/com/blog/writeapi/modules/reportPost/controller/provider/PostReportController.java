package com.blog.writeapi.modules.reportPost.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.reportPost.controller.doc.PostReportControllerDocs;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportPost.services.interfaces.IPostReportService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.PostReportMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/v1/post-report")
public class PostReportController implements PostReportControllerDocs {

    private final IPostReportService service;
    private final IPostService postService;
    private final PostReportMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreatePostReportDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();
        PostModel post = this.postService.getByIdSimple(dto.postId());

        Boolean exists = this.service.existsByPostAndUser(post, user);

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHttp<>(
                    null,
                    "You already report this post!",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        PostReportModel model = this.service.create(dto, post, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
            this.mapper.toDTO(model),
            "Report created with successfully",
            UUID.randomUUID().toString(),
            1,
            true,
            OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();

        PostReportModel report = this.service.findByIdSimple(id);

        this.service.delete(report, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Report deleted with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> patch(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdatePostReportDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel moderator = principal.getUser();

        PostReportModel report = this.service.findByIdSimple(id);

        PostReportModel reportUpdated = this.service.update(dto, report, moderator);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                this.mapper.toDTO(reportUpdated),
                "Report updated with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
