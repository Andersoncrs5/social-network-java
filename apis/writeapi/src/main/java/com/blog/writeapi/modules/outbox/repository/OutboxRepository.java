package com.blog.writeapi.modules.outbox.repository;

import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxRepository extends JpaRepository<OutboxModel, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM OutboxModel s WHERE s.id = :id")
    int deleteAndCount(@Param("id") @IsId Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM OutboxModel s WHERE s.status = :status")
    int deleteAllByStatus(@Param("status") OutboxStatus status);

    Page<OutboxModel> findAllByStatus(OutboxStatus status, Pageable pageable);
}
