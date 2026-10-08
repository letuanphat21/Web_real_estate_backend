package com.dat_viet_group.datvietgroup.modules.social.dto.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowResponse;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

@Component
public class FollowMapper {

    /**
     * @param userId     id của người kia (follower hoặc following)
     * @param user       thông tin user; null nếu không tìm thấy (khi đó chỉ trả userId)
     * @param followedAt thời điểm bắt đầu theo dõi
     */
    public FollowResponse toResponse(Long userId, User user, LocalDateTime followedAt) {
        return FollowResponse.builder()
                .userId(userId)
                .fullName(user == null ? null : user.getFullName())
                .avatarUrl(user == null ? null : user.getAvatarUrl())
                .followedAt(followedAt)
                .build();
    }
}
