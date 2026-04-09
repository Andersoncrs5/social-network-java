package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postReadingList.dto.PostReadingDTO;
import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {PostMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostReadingMapper {

    PostReadingDTO toDTO(@IsModelInitialized PostReadingListModel model);

}
