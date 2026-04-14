package com.blog.writeapi.modules.stories.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.stories.controller.docs.IStoryControllerDocs;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.mappers.StoryMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/story")
public class StoryController implements IStoryControllerDocs {

    private final IStoryService service;
    private final StoryMapper mapper;

    @Override
    public ResponseHttp<StoryDTO> create(
            @Valid @ModelAttribute CreateStoryDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();

        StoryModel model = this.service.create(user.getId(), dto);

        return ResponseHttp.success(this.mapper.toDTO(model), "Story created!");
    }

}
