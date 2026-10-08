package com.dat_viet_group.datvietgroup.modules.notification.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    // Từ UserNotification
    private long id;
    @JsonProperty("isRead")
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

    // Từ NotificationType: id để khớp tab (?typeId=) và chọn icon/màu, name để hiển thị
    private long notificationTypeId;
    private String notificationTypeName;
}