package com.blog.writeapi.modules.userCategoryPreference.gateway;

import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.category.service.docs.ICategoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCategoryPreferenceModuleGateway {

    private final IUserService userService;
    private final ICategoryService categoryService;

    public UserModel findByUserId(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public CategoryModel findByCategoryById(@IsId Long id) {
        return categoryService.getByIdSimple(id);
    }

}
