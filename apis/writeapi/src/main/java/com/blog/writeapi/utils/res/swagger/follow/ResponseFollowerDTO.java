package com.blog.writeapi.utils.res.swagger.follow;

import com.blog.writeapi.modules.followers.dtos.FollowersDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseFollowerDTO(
        ResponseHttp<FollowersDTO> followersDTOResponseHttp
) {
}
