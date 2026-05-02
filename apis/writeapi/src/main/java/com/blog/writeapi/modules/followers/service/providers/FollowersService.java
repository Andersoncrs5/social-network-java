package com.blog.writeapi.modules.followers.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.modules.followers.gateway.FollowModuleGateway;
import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.modules.followers.repository.FollowersRepository;
import com.blog.writeapi.modules.followers.service.interfaces.IFollowersService;
import com.blog.writeapi.modules.metric.dto.UserMetricEventDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.FollowersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowersService implements IFollowersService {

    private final FollowModuleGateway gateway;
    private final FollowersRepository repository;
    private final Snowflake generator;
    private final FollowersMapper mapper;

    @Override
    public FollowersModel getByIdSimple(@IsId Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Follow not found")
        );
    }

    @Override
    public Optional<FollowersModel> getByFollowerAndFollowing(
            @IsModelInitialized UserModel follower,
            @IsModelInitialized UserModel following
    ) {
        return repository.findByFollowerAndFollowing(
                follower, following
        );
    }

    @Override
    public void delete(@IsModelInitialized FollowersModel follow) {
        this.repository.delete(follow);

        gateway.handleMetricUser(UserMetricEventDTO.create(
                follow.getFollower().getId(),
                UserMetricEnum.FOLLOWING,
                ActionEnum.RED
        ));

        gateway.handleMetricUser(UserMetricEventDTO.create(
                follow.getFollowing().getId(),
                UserMetricEnum.FOLLOW,
                ActionEnum.RED
        ));
    }

    @Override
    public boolean deleteIfExist(
            @IsId Long followerId,
            @IsId Long followingId
    ) {
        Optional<FollowersModel> exists = repository.findByFollowerIdAndFollowingId(followerId, followingId);
        exists.ifPresent(this::delete);
        return exists.isPresent();
    }

    public void deleteFollowRelationships(
            @IsId Long followerId,
            @IsId Long followingId
    ) {
        this.deleteIfExist(followerId, followingId);
        this.deleteIfExist(followingId, followerId);
    }

    @Override
    public FollowersModel create(
            @IsModelInitialized UserModel follower,
            @IsModelInitialized UserModel following
    ) {

        if (Objects.equals(follower.getId(), following.getId())) {
            throw new BusinessRuleException("You cannot follow yourself.");
        }

        FollowersModel follow = new FollowersModel().toBuilder()
                .id(this.generator.nextId())
                .following(following)
                .follower(follower)
                .build();

        try {
            FollowersModel save = this.repository.save(follow);

            gateway.handleMetricUser(UserMetricEventDTO.create(
                    follow.getFollower().getId(),
                    UserMetricEnum.FOLLOWING,
                    ActionEnum.SUM
            ));

            gateway.handleMetricUser(UserMetricEventDTO.create(
                    follow.getFollowing().getId(),
                    UserMetricEnum.FOLLOW,
                    ActionEnum.SUM
            ));

            return save;
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_followers")) {
                throw new UniqueConstraintViolationException("You are already following this user.");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating follow relationship: ", e);
            throw new InternalServerErrorException("Error processing follow request.");
        }
    }

    @Override
    public FollowersModel update(
            @IsModelInitialized FollowersModel follow,
            UpdateFollowersDTO dto
    ) {
        this.mapper.merge(dto, follow);

        try {
            return this.repository.save(follow);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("Cannot update follow status due to data integrity violation.");
        } catch (Exception e) {
            log.error("Error updating follow relationship: ", e);
            throw new InternalServerErrorException("Error updating follow status.");
        }
    }

}
