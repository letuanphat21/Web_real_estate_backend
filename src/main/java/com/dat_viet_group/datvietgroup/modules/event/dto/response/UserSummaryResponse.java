package com.dat_viet_group.datvietgroup.modules.event.dto.response;

import com.dat_viet_group.datvietgroup.modules.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSummaryResponse {

    private long id;
    private String fullName;
    private String avatarUrl;

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getId(), user.getFullName(), user.getAvatarUrl());
    }
}
