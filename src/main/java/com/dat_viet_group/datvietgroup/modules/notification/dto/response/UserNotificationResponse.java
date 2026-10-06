package com.dat_viet_group.datvietgroup.modules.notification.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    // Từ UserNotification
    private long id;
    private boolean isRead;
    private LocalDateTime readAt;

    // Từ Notification
    private long notificationId;
    private String title;
    private String content;
    private String image;
    private TargetType targetType;
    private Long targetId;
    private String actionUrl;
    private LocalDateTime createdAt;

    // Từ NotificationType
    private String notificationTypeName;
}