package com.blog.writeapi.utils.res.swagger.tag;

import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseTagDTO(
        ResponseHttp<TagDTO> dto
) {
}
