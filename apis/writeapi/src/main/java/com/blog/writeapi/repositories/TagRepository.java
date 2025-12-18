package com.blog.writeapi.repositories;

import com.blog.writeapi.models.TagModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<@NonNull TagModel, @NonNull Long> {
}
