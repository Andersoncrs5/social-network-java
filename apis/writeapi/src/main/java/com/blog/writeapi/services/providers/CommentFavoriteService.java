package com.blog.writeapi.services.providers;

import com.blog.writeapi.repositories.CommentFavoriteRepository;
import com.blog.writeapi.services.interfaces.ICommentFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentFavoriteService implements ICommentFavoriteService {

    private final CommentFavoriteRepository repository;

}
