package com.blog.writeapi.modules.stories.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public interface IStoryControllerDocs {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CircuitBreaker(name = "tag-upload-file-cb")
    ResponseHttp<StoryDTO> create(
            @Valid @ModelAttribute CreateStoryDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
