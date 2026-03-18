package com.blog.writeapi.modules.postReportType.service.interfaces;

import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

import java.util.Optional;

public interface IPostReportTypeService  {
    Optional<PostReportTypeModel> getById(@IsId Long id);
}
