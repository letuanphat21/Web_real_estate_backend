package com.dat_viet_group.datvietgroup.modules.notification.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private long id;

    // Gộp phẳng từ NotificationType để frontend khỏi lồng object
    private long notificationTypeId;
    private String notificationTypeName;

    // ID người tạo, chỉ là số (module User nằm riêng)
    private Long createdBy;

    private String title;
    private String content;
    private String image;
    private TargetType targetType;
    private Long targetId;
    private String actionUrl;
    private LocalDateTime createdAt;
}