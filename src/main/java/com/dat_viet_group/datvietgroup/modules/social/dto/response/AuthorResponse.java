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
public class AuthorResponse {

    private Long id;
    private String fullName;
    private String avatarUrl;
}
