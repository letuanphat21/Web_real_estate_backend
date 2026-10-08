package com.dat_viet_group.datvietgroup.modules.social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsResponse {

    private Long userId;
    private long followerCount;
    private long followingCount;
    /** Người dùng hiện tại có đang theo dõi userId này không. */
    private boolean following;
}
