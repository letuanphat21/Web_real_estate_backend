package com.dat_viet_group.datvietgroup.modules.notification.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeResponse {

    private long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
}