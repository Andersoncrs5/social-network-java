package com.blog.writeapi.services.providers;

import com.blog.writeapi.repositories.PostReactionRepository;
import com.blog.writeapi.services.interfaces.IPostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostReactionService implements IPostReactionService {

    private final PostReactionRepository repository;

}
