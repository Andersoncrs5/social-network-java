package com.blog.writeapi.modules.postVote.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.postVote.controller.docs.PostVoteControllerDocs;
import com.blog.writeapi.modules.postVote.dtos.TogglePostVoteDTO;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import com.blog.writeapi.modules.postVote.service.docs.IPostVoteService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.PostVoteMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-vote")
public class PostVoteController implements PostVoteControllerDocs {

    private final IPostVoteService service;
    private final IPostService postService;
    private final PostVoteMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @Valid @RequestBody TogglePostVoteDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PostModel post = this.postService.getByIdSimple(dto.postID());
        UserModel user = principal.getUser();

        ResultToggle<PostVoteModel> toggle = this.service.toggle(user, post, dto);

        String message = switch (toggle.result()) {
            case UPDATED -> "Vote updated";
            case REMOVED -> "Vote removed successfully";
            case ADDED -> "Vote applied successfully";
        };

        HttpStatus status = (toggle.result() == ToggleEnum.ADDED)
                ? HttpStatus.CREATED
                : HttpStatus.OK;

        var responseDto = toggle.body().map(this.mapper::toDTO).orElse(null);

        return ResponseEntity.status(status)
                .body(ResponseHttp.success(responseDto, message));
    }



}
