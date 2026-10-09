package com.dat_viet_group.datvietgroup.modules.social.dto.mapper;

import org.springframework.stereotype.Component;

import com.dat_viet_group.datvietgroup.modules.social.dto.response.AuthorResponse;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;

@Component
public class AuthorMapper {

    /**
     * @param userId id người viết
     * @param user   thông tin user; null nếu không tìm thấy (khi đó chỉ trả id)
     */
    public AuthorResponse toResponse(Long userId, User user) {
        return AuthorResponse.builder()
                .id(userId)
                .fullName(user == null ? null : user.getFullName())
                .avatarUrl(user == null ? null : user.getAvatarUrl())
                .build();
    }
}
