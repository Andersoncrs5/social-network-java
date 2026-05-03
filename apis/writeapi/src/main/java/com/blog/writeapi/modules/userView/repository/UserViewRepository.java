package com.blog.writeapi.modules.userView.repository;

import com.blog.writeapi.modules.userView.model.UserViewModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserViewRepository extends JpaRepository<UserViewModel, Long> {
    boolean existsByViewerIdAndViewedId(Long viewerId, Long viewedId);
}
