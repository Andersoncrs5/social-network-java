package com.blog.writeapi.modules.userView.service.interfaces;

import com.blog.writeapi.modules.userView.model.UserViewModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.result.Result;

public interface IUserViewService {
    Result<UserViewModel> createIfNotExists(
            @IsId Long viewerId,
            @IsId Long viewedId
    );
}
