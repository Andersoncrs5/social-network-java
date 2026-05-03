package com.blog.writeapi.modules.userView.service.provider;

import com.blog.writeapi.modules.metric.dto.UserMetricEventDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userView.gateway.UserViewModuleGateway;
import com.blog.writeapi.modules.userView.model.UserViewModel;
import com.blog.writeapi.modules.userView.repository.UserViewRepository;
import com.blog.writeapi.modules.userView.service.interfaces.IUserViewService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service @Validated
@RequiredArgsConstructor
public class UserViewService implements IUserViewService {

    private final UserViewRepository repository;
    private final UserViewModuleGateway gateway;

    public Result<UserViewModel> create(
            @IsId Long viewerId,
            @IsId Long viewedId
    ) {
        if (Objects.equals(viewerId, viewedId)) {
            return Result.badRequest("You cannot record a view on your own profile.");
        }

        UserModel viewer = this.gateway.findUserById(viewerId);
        UserModel viewed = this.gateway.findUserById(viewedId);

        UserViewModel model = new UserViewModel().toBuilder()
                .viewed(viewed)
                .viewer(viewer)
                .amount(1L)
                .build();

        try {
            UserViewModel save = repository.save(model);

            gateway.handleMetricUser(UserMetricEventDTO.create(
                    viewedId,
                    UserMetricEnum.USER_VIEW_RECEIVED,
                    ActionEnum.SUM
            ));

            return Result.created(save);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_user_view")) {
                return Result.conflict("This user has already been added to this view.");
            }

            return Result.badRequest("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating user view relationship", e);
            return Result.failure(HttpStatus.INTERNAL_SERVER_ERROR, "", "Error creating view association.");
        }
    }

    public Result<UserViewModel> createIfNotExists(
            @IsId Long viewerId,
            @IsId Long viewedId
    ) {
        boolean exists = this.repository.existsByViewerIdAndViewedId(viewerId, viewedId);

        if (exists) return Result.success();

        return this.create(viewerId, viewedId);
    }

}
