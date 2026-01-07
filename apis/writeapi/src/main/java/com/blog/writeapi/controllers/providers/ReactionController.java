package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.ReactionControllerDocs;
import com.blog.writeapi.services.interfaces.IReactionService;
import com.blog.writeapi.services.providers.ReactionService;
import com.blog.writeapi.utils.mappers.ReactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reaction")
public class ReactionController implements ReactionControllerDocs {

    private final IReactionService service;
    private final ReactionMapper mapper;



}
