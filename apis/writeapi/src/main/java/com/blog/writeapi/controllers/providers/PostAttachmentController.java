package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostAttachmentControllerDocs;
import com.blog.writeapi.services.interfaces.IPostAttachmentService;
import com.blog.writeapi.utils.mappers.PostAttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-attachment")
public class PostAttachmentController implements PostAttachmentControllerDocs {

    private final IPostAttachmentService service;
    private final PostAttachmentMapper mapper;

}
