package com.dat_viet_group.datvietgroup.modules.user.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private String email;
    private String fullName;
    private String phone;
    private String role;
}
