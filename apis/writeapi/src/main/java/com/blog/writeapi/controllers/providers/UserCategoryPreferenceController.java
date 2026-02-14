package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.UserCategoryPreferenceControllerDocs;
import com.blog.writeapi.dtos.userCategoryPreference.UserCategoryPreferenceDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ICategoryService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserCategoryPreferenceService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.mappers.UserCategoryPreferenceMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-category-preference")
public class UserCategoryPreferenceController implements UserCategoryPreferenceControllerDocs {

    private final IUserCategoryPreferenceService service;
    private final ICategoryService categoryService;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final UserCategoryPreferenceMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long categoryId,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userId);
        CategoryModel category = this.categoryService.getByIdSimple(categoryId);

        Optional<UserCategoryPreferenceModel> optional = this.service.getByUserAndCategory(user, category);

        if (optional.isPresent()) {
            this.service.delete(optional.get());

            return ResponseEntity.status(HttpStatusCode.OK).body(new ResponseHttp<>(
                    null,
                    "Category removed with successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
            ));
        }

        UserCategoryPreferenceModel model = this.service.create(user, category);

        UserCategoryPreferenceDTO dto = this.mapper.toDTO(model);

        return ResponseEntity.status(HttpStatusCode.CREATED).body(new ResponseHttp<>(
                dto,
            "Category added with successfully",
            UUID.randomUUID().toString(),
            1,
            true,
            OffsetDateTime.now()
        ));
    }

}
