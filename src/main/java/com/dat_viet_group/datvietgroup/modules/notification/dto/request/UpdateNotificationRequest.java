package com.dat_viet_group.datvietgroup.modules.notification.dto.request;

import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationRequest {

    private Long notificationTypeId;

    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    private String content;

    @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
    private String image;

    private TargetType targetType;

    private Long targetId;

    @Size(max = 500, message = "Đường dẫn hành động tối đa 500 ký tự")
    private String actionUrl;
}