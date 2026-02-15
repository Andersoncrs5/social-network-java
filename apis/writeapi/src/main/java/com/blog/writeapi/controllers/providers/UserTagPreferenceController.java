package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.UserTagPreferenceControllerDocs;
import com.blog.writeapi.dtos.userTagPreference.UserTagPreferenceDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.models.UserTagPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ITagService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserTagPreferenceService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.mappers.UserTagPreferenceMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-tag-preference")
public class UserTagPreferenceController implements UserTagPreferenceControllerDocs {

    private final IUserTagPreferenceService service;
    private final ITagService categoryService;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final UserTagPreferenceMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long tagID,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userId);
        TagModel category = this.categoryService.getByIdSimple(tagID);

        Optional<UserTagPreferenceModel> optional = this.service.getByUserAndTag(user, category);

        if (optional.isPresent()) {
            this.service.delete(optional.get());

            return ResponseEntity.status(HttpStatusCode.OK).body(new ResponseHttp<>(
                    null,
                    "Tag removed with successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
            ));
        }

        UserTagPreferenceModel model = this.service.create(user, category);

        UserTagPreferenceDTO dto = this.mapper.toDTO(model);

        return ResponseEntity.status(HttpStatusCode.CREATED).body(new ResponseHttp<>(
                dto,
                "Tag added with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
