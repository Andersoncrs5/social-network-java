package com.blog.writeapi.services.providers;

import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.repositories.PostCategoriesRepository;
import com.blog.writeapi.services.interfaces.IPostCategoriesService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCategoriesService implements IPostCategoriesService {

    private final PostCategoriesRepository repository;

    @Transactional(readOnly = true)
    public Optional<PostCategoriesModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }
}
