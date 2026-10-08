package com.dat_viet_group.datvietgroup.modules.notification.dto.request;

import java.util.List;

import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull (message = "Loại thông báo không được để trống")
    private Long notificationTypeId;

    @NotBlank (message = "Tiêu đề không được để trống")
    @Size(max = 255, message="Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotBlank (message = "Nội dung không được để trống")
    private String content;

    @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
    private String image;

    private TargetType targetType;

    private Long targetId;

    @Size (max = 500 , message = "Đường dẫn hành động tối đa 500 ký tự")
    private String actionUrl;

    // Dsach ID user nhận thông báo
    private List<Long> userIds;


}