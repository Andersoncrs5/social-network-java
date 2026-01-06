package com.blog.writeapi.utils.res.swagger.commentFavorite;

import com.blog.writeapi.dtos.commentFavorite.CommentFavoriteDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseCommentFavorite(
        ResponseHttp<CommentFavoriteDTO> res
) {
}
