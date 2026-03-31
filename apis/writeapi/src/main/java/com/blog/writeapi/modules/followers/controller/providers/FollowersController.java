package com.blog.writeapi.modules.followers.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.followers.controller.docs.FollowersControllerDocs;
import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.modules.followers.service.interfaces.IFollowersService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.mappers.FollowersMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/follow")
public class FollowersController implements FollowersControllerDocs {

    private final IUserService userService;
    private final IFollowersService service;
    private final ITokenService tokenService;
    private final FollowersMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
        @PathVariable Long followingId,
        HttpServletRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long followId = principal.getId();

        if (Objects.equals(followId, followingId)) {
            throw new BusinessRuleException("You cannot follow yourself.");
        }

        UserModel follow = principal.getUser();
        UserModel following = this.userService.GetByIdSimple(followingId);

        Optional<FollowersModel> optional = this.service.getByFollowerAndFollowing(follow, following);

        if (optional.isPresent()) {
            this.service.delete(optional.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Unfollow with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
            ));
        }

        FollowersModel model = this.service.create(follow, following);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
            mapper.toDTO(model),
            "Follow with successfully",
            UUID.randomUUID().toString(),
            1,
            true,
            OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UpdateFollowersDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userID = principal.getId();
        FollowersModel follow = this.service.getByIdSimple(id);

        if (!Objects.equals(follow.getFollower().getId(), userID)) {
            throw new ResourceOwnerMismatchException("You cannot to do this action");
        }

        FollowersModel updated = this.service.update(follow, dto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                mapper.toDTO(updated),
                "Follow updated with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
