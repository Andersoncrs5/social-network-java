package com.blog.writeapi.modules.postReadingList.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.postReadingList.controller.docs.IPostReadingListControllerDocs;
import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.modules.postReadingList.service.interfaces.IPostReadingListService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.PostReadingMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-reading-list")
public class PostReadingListController implements IPostReadingListControllerDocs {

    private final PostReadingMapper mapper;
    private final IPostReadingListService service;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ResultToggle<PostReadingListModel> toggle = this.service.toggle(principal.getId(), postId);

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Post added" : "Post removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(new ResponseHttp<>(
                null,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));


    }

}
