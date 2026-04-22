package com.blog.writeapi.modules.userBlock.service.docs;

import com.blog.writeapi.modules.userBlock.model.UserBlockModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

public interface IUserBlockService {
    void delete(
            @IsModelInitialized UserBlockModel block
    );
    ResultToggle<UserBlockModel> toggle(
            @IsId Long blockerId,
            @IsId Long blockedId
    );
    boolean isBlocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    );
}
