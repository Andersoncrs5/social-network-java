package com.blog.writeapi.utils.res.swagger.postVote;

import com.blog.writeapi.dtos.postVote.PostVoteDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponsePostVoteDTO(
        ResponseHttp<PostVoteDTO> http
) {
}
