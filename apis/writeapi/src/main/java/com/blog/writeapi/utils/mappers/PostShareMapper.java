package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postShare.dto.PostShareDTO;
import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        uses = { UserMapper.class, PostMapper.class },
        componentModel = "spring"
)
public interface PostShareMapper {

    PostShareDTO toDTO(@IsModelInitialized PostShareModel model);

}
