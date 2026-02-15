package com.blog.writeapi.utils.res.swagger.category;

import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseCategoryDTO(
        ResponseHttp<CategoryDTO> res
) {
}
