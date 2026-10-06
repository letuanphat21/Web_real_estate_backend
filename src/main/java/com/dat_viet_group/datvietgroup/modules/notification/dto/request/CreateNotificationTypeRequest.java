package com.dat_viet_group.datvietgroup.modules.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationTypeRequest {

    @NotBlank(message = "Tên loại thông báo không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    // Dùng @Builder.Default để giữ giá trị mặc định khi dùng builder
    @Builder.Default
    private Boolean isActive = true;
}