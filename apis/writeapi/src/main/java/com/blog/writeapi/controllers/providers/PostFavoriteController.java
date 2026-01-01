package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostFavoriteControllerDocs;
import com.blog.writeapi.services.providers.PostFavoriteService;
import com.blog.writeapi.utils.mappers.PostFavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-favorite")
public class PostFavoriteController implements PostFavoriteControllerDocs {

    private final PostFavoriteService service;
    private final PostFavoriteMapper mapper;


}
