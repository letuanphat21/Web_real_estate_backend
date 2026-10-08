package com.dat_viet_group.datvietgroup.modules.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowResponse;
import com.dat_viet_group.datvietgroup.modules.social.dto.response.FollowStatsResponse;

public interface FollowService {

    /** Bật/tắt theo dõi: chưa theo dõi thì theo dõi, đang theo dõi thì bỏ theo dõi. */
    FollowStatsResponse toggleFollow(String emailOrPhone, long targetUserId);

    /** Những người đang theo dõi userId. */
    Page<FollowResponse> getFollowers(long userId, Pageable pageable);

    /** Những người userId đang theo dõi. */
    Page<FollowResponse> getFollowing(long userId, Pageable pageable);

    /** Số follower, số following của userId và trạng thái theo dõi của người dùng hiện tại. */
    FollowStatsResponse getStats(String emailOrPhone, long userId);
}
