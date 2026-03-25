package com.blog.writeapi.modules.userBlock.service.provider;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userBlock.gateway.UserBlockModuleGateway;
import com.blog.writeapi.modules.userBlock.model.UserBlockModel;
import com.blog.writeapi.modules.userBlock.repository.UserBlockRepository;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBlockService implements IUserBlockService {

    private final UserBlockRepository repository;
    private final UserBlockModuleGateway gateway;

    public void delete(@IsModelInitialized UserBlockModel block) {
        this.repository.delete(block);
    }

    public Optional<UserBlockModel> findByBlockerIdAndBlockedId(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.repository.findByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    public UserBlockModel create(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        UserModel blocked = this.gateway.findByUserId(blockedId);
        UserModel blocker = this.gateway.findByUserId(blockerId);

        return createSimple(blocked, blocker);
    }

    public UserBlockModel createSimple(
            @IsModelInitialized UserModel blocked,
            @IsModelInitialized UserModel blocker
    ) {
        if (Objects.equals(blocked.getId(), blocker.getId())) {
            throw new BusinessRuleException("You cannot to block yourself", HttpStatus.FORBIDDEN);
        }

        UserBlockModel model = UserBlockModel.builder()
                .id(gateway.generateId())
                .blocked(blocked)
                .blocker(blocker)
                .build();

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("idx_user_blocked_keys")) {
                throw new UniqueConstraintViolationException(
                        "This blocked already has this user"
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating CommentReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    public ResultToggle<UserBlockModel> toggle(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        UserModel blocked = this.gateway.findByUserId(blockedId);
        UserModel blocker = this.gateway.findByUserId(blockerId);

        Optional<UserBlockModel> optional = this.repository.findByBlockerAndBlocked(blocker, blocked);

        if (optional.isPresent()) {
            this.repository.delete(optional.get());

            return ResultToggle.removed();
        }

        UserBlockModel simple = this.createSimple(blocked, blocker);

        return ResultToggle.added(simple);
    }

}
