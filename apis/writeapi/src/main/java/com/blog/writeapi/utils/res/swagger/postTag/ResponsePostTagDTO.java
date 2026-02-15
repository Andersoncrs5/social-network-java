package com.blog.writeapi.utils.res.swagger.postTag;

import com.blog.writeapi.modules.postTag.dtos.PostTagDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponsePostTagDTO(
        ResponseHttp<PostTagDTO> dto
) {
}
