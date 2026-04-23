package com.blog.writeapi.modules.adm.service.docs;

import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.result.Result;

public interface IAdmService {
    Result<UserRoleModel> addRoleToUser(
            ToggleRoleDTO dto
    );
    Result<Void> removeRoleToUser(
            ToggleRoleDTO dto
    );
    ResultToggle<UserRoleModel> toggleRoleAdmInUser(ToggleRoleDTO dto, Long currentUserId);
}
