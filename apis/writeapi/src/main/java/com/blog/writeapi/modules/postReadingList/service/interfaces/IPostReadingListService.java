package com.blog.writeapi.modules.postReadingList.service.interfaces;

import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;

public interface IPostReadingListService {
    ResultToggle<PostReadingListModel> toggle(@IsId Long userId, @IsId Long postId);
}
