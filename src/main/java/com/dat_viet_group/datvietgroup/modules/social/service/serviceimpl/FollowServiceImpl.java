package com.dat_viet_group.datvietgroup.modules.social.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.social.dao.UserFollowRepository;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowStatsResponse;
import com.dat_viet_group.datvietgroup.modules.social.entity.UserFollow;
import com.dat_viet_group.datvietgroup.modules.social.dto.mapper.FollowMapper;
import com.dat_viet_group.datvietgroup.modules.social.service.FollowService;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserService userService;
    private final FollowMapper followMapper;

    @Override
    @Transactional
    public FollowStatsResponse toggleFollow(String emailOrPhone, long targetUserId) {
        long currentUserId = userService.findByEmailOrPhone(emailOrPhone).getId();
        if (currentUserId == targetUserId) {
            throw new AppException(ErrorCode.FOLLOW_SELF);
        }
        userService.findById(targetUserId); // ném USER_NOT_FOUND nếu không tồn tại

        Optional<UserFollow> existing = userFollowRepository.findByFollowerAndFollowing(currentUserId, targetUserId);
        boolean following;
        if (existing.isPresent()) {
            userFollowRepository.delete(existing.get());
            following = false;
        } else {
            UserFollow follow = new UserFollow();
            follow.setFollowerId(currentUserId);
            follow.setFollowingId(targetUserId);
            follow.setCreatedAt(LocalDateTime.now());
            userFollowRepository.save(follow);
            following = true;
        }
        userFollowRepository.flush();

        return toStats(targetUserId, following);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowers(long userId, Pageable pageable) {
        userService.findById(userId);
        return toPage(userFollowRepository.findFollowers(userId, pageable), UserFollow::getFollowerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowing(long userId, Pageable pageable) {
        userService.findById(userId);
        return toPage(userFollowRepository.findFollowing(userId, pageable), UserFollow::getFollowingId);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatsResponse getStats(String emailOrPhone, long userId) {
        userService.findById(userId);
        long currentUserId = userService.findByEmailOrPhone(emailOrPhone).getId();
        return toStats(userId, userFollowRepository.isFollowing(currentUserId, userId));
    }

    /** Lấy user của cả trang trong 1 truy vấn rồi map, tránh gọi DB từng dòng. */
    private Page<FollowResponse> toPage(Page<UserFollow> page, Function<UserFollow, Long> otherUserId) {
        Set<Long> ids = page.getContent().stream().map(otherUserId).collect(Collectors.toSet());
        Map<Long, User> users = userService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return page.map(f -> followMapper.toResponse(otherUserId.apply(f), users.get(otherUserId.apply(f)),
                f.getCreatedAt()));
    }

    private FollowStatsResponse toStats(long userId, boolean following) {
        return FollowStatsResponse.builder()
                .userId(userId)
                .followerCount(userFollowRepository.countFollowers(userId))
                .followingCount(userFollowRepository.countFollowing(userId))
                .following(following)
                .build();
    }
}
