package com.dat_viet_group.datvietgroup.modules.social.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Một dòng trong danh sách follower / following: thông tin người kia + thời điểm bắt đầu theo dõi. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {

    private Long userId;
    private String fullName;
    private String avatarUrl;
    private LocalDateTime followedAt;
}
