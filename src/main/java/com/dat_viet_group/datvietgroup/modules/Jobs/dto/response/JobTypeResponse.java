package com.dat_viet_group.datvietgroup.modules.Jobs.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobTypeResponse {

    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
