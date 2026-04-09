package com.blog.writeapi.modules.postReadingList.repository;

import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReadingListRepository extends JpaRepository<PostReadingListModel, Long> {

    Optional<PostReadingListModel> findByUserIdAndPostId(Long userId, Long postId);
}
