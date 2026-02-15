package com.blog.writeapi.utils.res.swagger.post;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponsePostDTO(
        ResponseHttp<PostDTO> dto
) {
}
